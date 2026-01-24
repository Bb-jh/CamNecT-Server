package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.*;
import CamNecT.CamNecT_Server.domain.community.event.CommentAcceptedEvent;
import CamNecT.CamNecT_Server.domain.community.model.*;
import CamNecT.CamNecT_Server.domain.community.model.Comments.AcceptedComments;
import CamNecT.CamNecT_Server.domain.community.model.Comments.Comments;
import CamNecT.CamNecT_Server.domain.community.model.Posts.*;
import CamNecT.CamNecT_Server.domain.community.model.enums.*;
import CamNecT.CamNecT_Server.domain.community.repository.*;
import CamNecT.CamNecT_Server.domain.community.repository.Comments.AcceptedCommentsRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Comments.CommentsRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.*;
import CamNecT.CamNecT_Server.domain.point.model.PointEvent;
import CamNecT.CamNecT_Server.domain.point.service.PointService;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.ErrorCode;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final BoardsRepository boardsRepository;
    private final PostsRepository postsRepository;

    private final PostStatsRepository postStatsRepository;
    private final PostLikesRepository postLikesRepository;

    private final TagRepository tagRepository;
    private final PostTagsRepository postTagsRepository;

    private final CommentsRepository commentsRepository;
    private final AcceptedCommentsRepository acceptedCommentsRepository;

    private final PostAttachmentsRepository postAttachmentsRepository;
    private final PostAttachmentsService postAttachmentsService;
    private final PostBookmarksRepository postBookmarksRepository;
    private final PostAccessRepository postAccessRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final PointService pointService;

    @Transactional
    @Override
    public CreatePostResponse create(Long userId, CreatePostRequest req) {
        // TODO: 인증 붙이면 userId를 SecurityContext에서 꺼내도록 변경
        if (userId == null) userId = 1L;

        Boards board = boardsRepository.findByCode(req.boardCode())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        PostAccessType accessType = (req.accessType() == null) ? PostAccessType.FREE : req.accessType();
        Integer requiredPoints = req.requiredPoints();

        if (accessType == PostAccessType.POINT_REQUIRED) {
            if (requiredPoints == null || requiredPoints <= 0) {
                throw new CustomException(ErrorCode.INVALID_REQUIRED_POINTS);
            }
        } else {
            requiredPoints = null; // FREE면 비용 제거
        }

        Posts post = Posts.create(board, userId, req.title(), req.content(), Boolean.TRUE.equals(req.anonymous()));

        post.applyAccess(accessType, requiredPoints);

        Posts saved = postsRepository.save(post);

        // stats 생성(필수)
        postStatsRepository.save(PostStats.init(saved));

        // 태그 연결
        replaceTags(saved, req.tagIds());

        //첨부
        postAttachmentsService.replace(saved, req.attachments());

        // 첨부는 PostAttachmentsService로 분리하거나, 기존 로직이 있으면 여기서 호출
        return new CreatePostResponse(saved.getId());
    }

    @Transactional
    @Override
    public void update(Long userId, Long postId, UpdatePostRequest req) {
        if (userId == null) userId = 1L;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!Objects.equals(post.getUserId(), userId)) {
            throw new CustomException(ErrorCode.POST_FORBIDDEN);
        }

        post.update(req.title(), req.content(), req.anonymous());

        // 태그는 “요청이 들어온 경우”만 교체
        if (req.tagIds() != null) {
            postTagsRepository.deleteByPost_Id(postId);
            replaceTags(post, req.tagIds());
        }

        // 첨부 "요청이 들어온 경우만"
        if (req.attachments() != null) {
            postAttachmentsService.replace(post, req.attachments());
        }

        touchStats(postId);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long postId) {
        if (userId == null) userId = 1L;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!Objects.equals(post.getUserId(), userId)) {
            throw new CustomException(ErrorCode.POST_FORBIDDEN);
        }

        if (post.getBoard().getCode() == BoardCode.QUESTION
                && acceptedCommentsRepository.existsByPost_Id(postId)) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_ACCEPTED_QUESTION);
        }

        post.deleteSoft();

        postAttachmentsRepository.softDeleteByPostId(postId);

        // 연관 테이블 정리(필요한 것만)
        postTagsRepository.deleteByPost_Id(postId);
        acceptedCommentsRepository.deleteByPost_Id(postId);
    }

    @Transactional
    @Override
    public ToggleLikeResponse toggleLike(Long userId, Long postId) {
        if (userId == null) userId = 1L;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        PostStats stats = getOrCreateStats(post);

        boolean liked;
        if (postLikesRepository.existsByPost_IdAndUserId(postId, userId)) {
            postLikesRepository.deleteByPost_IdAndUserId(postId, userId);
            stats.decLike();
            liked = false;
        } else {
            postLikesRepository.save(PostLikes.of(post, userId));
            stats.incLike();
            liked = true;
        }

        return new ToggleLikeResponse(liked, stats.getLikeCount());
    }

    public PostDetailResponse getDetail(Long userId, Long postId) {

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new CustomException(ErrorCode.POST_NOT_PUBLISHED);
        }

        PostStats stats = getOrCreateStats(post);
        stats.incView();

        boolean likedByMe = (userId != null) &&
                postLikesRepository.existsByPost_IdAndUserId(postId, userId);

        List<Long> tagIds = postTagsRepository.findByPost_Id(postId).stream()
                .map(pt -> pt.getTag().getId())
                .toList();

        Long acceptedCommentId = acceptedCommentsRepository.findByPost_Id(postId)
                .map(ac -> ac.getComment().getId())
                .orElse(null);

        ContentAccessStatus accessStatus = null;
        Integer requiredPoints = null;
        Integer myPoints = null;

        if (post.getAccessType() == PostAccessType.POINT_REQUIRED) {
            requiredPoints = post.getRequiredPoints();

            if (requiredPoints == null || requiredPoints <= 0) {
                throw new CustomException(ErrorCode.INTERNAL_ERROR);
            }

            if (userId == null) {
                accessStatus = ContentAccessStatus.LOGIN_REQUIRED;
            } else if (userId.equals(post.getUserId())) {
                accessStatus = ContentAccessStatus.GRANTED; // 작성자는 무료
            } else if (postAccessRepository.existsByPost_IdAndUserId(postId, userId)) {
                accessStatus = ContentAccessStatus.GRANTED; // 구매함
            } else {
                myPoints = pointService.getBalance(userId);
                accessStatus = (myPoints >= requiredPoints)
                        ? ContentAccessStatus.NEED_PURCHASE
                        : ContentAccessStatus.INSUFFICIENT_POINTS;
            }
        }

        String content = (accessStatus == ContentAccessStatus.GRANTED) ? post.getContent() : null;

        return new PostDetailResponse(
                post.getId(),
                post.getBoard().getCode(),
                post.getTitle(),
                content,
                post.isAnonymous(),
                post.getUserId(),
                stats.getViewCount(),
                stats.getLikeCount(),
                likedByMe,
                acceptedCommentId,
                tagIds,
                accessStatus,
                requiredPoints,
                myPoints
        );
    }

    @Transactional
    @Override
    public void acceptComment(Long userId, Long postId, Long commentId) {
        if (userId == null) userId = 1L;


        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (post.getBoard().getCode() != BoardCode.QUESTION) {
            throw new CustomException(ErrorCode.ONLY_QUESTION_CAN_ACCEPT);
        }
        // 질문 작성자만 채택 가능
        if (!Objects.equals(post.getUserId(), userId)) {
            throw new CustomException(ErrorCode.POST_FORBIDDEN);
        }

        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!Objects.equals(comment.getPost().getId(), postId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_IN_POST);
        }

        // (선택) 삭제/숨김 댓글 채택 금지
        if (comment.getStatus() != CommentStatus.PUBLISHED) {
            throw new CustomException(ErrorCode.CANNOT_ACCEPT_UNPUBLISHED_COMMENT);
        }

        try {
            acceptedCommentsRepository.save(AcceptedComments.of(post, comment, userId));
        } catch (DataIntegrityViolationException e) {
            // 유니크(post_id) 위반이면 여기로 들어옴
            throw new CustomException(ErrorCode.ALREADY_ACCEPTED, e);
        }
        touchStats(postId);

        Long receiverId = comment.getUserId();
        if (receiverId != null && !Objects.equals(receiverId, userId)) {
            eventPublisher.publishEvent(new CommentAcceptedEvent(receiverId, postId, commentId, userId));
        }
    }

    // -------------------
    // helpers
    // -------------------
    private void replaceTags(Posts post, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;

        List<Long> ids = tagIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) return;

        List<Tag> tags = tagRepository.findAllById(ids);
        if (tags.size() != ids.size()) {
            throw new CustomException(ErrorCode.INVALID_TAG_IDS);
        }

        for (Tag t : tags) {
            if (!t.isActive()) throw new CustomException(ErrorCode.INACTIVE_TAG);
            postTagsRepository.save(PostTags.link(post, t));
        }
    }

    @Transactional
    public ToggleBookmarkResponse toggleBookmark(Long userId, Long postId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        PostStats stats = postStatsRepository.findByPost_Id(postId)
                .orElseGet(() -> postStatsRepository.save(PostStats.init(post)));

        boolean exists = postBookmarksRepository.existsByPost_IdAndUserId(postId, userId);

        if (exists) {
            postBookmarksRepository.deleteByPost_IdAndUserId(postId, userId);
            stats.decBookmark();
        } else {
            postBookmarksRepository.save(PostBookmarks.create(post, userId));
            stats.incBookmark();
        }

        // stats 저장(더티체킹이면 없어도 되지만 명시적으로)
        postStatsRepository.save(stats);

        return new ToggleBookmarkResponse(postId, !exists, stats.getBookmarkCount());
    }

    private void touchStats(Long postId) {
        postStatsRepository.findByPost_Id(postId).ifPresent(PostStats::touch);
    }


    // =========================
    // 정보글 구매(열람권 생성)
    // =========================
    @Transactional
    public PurchasePostAccessResponse purchasePostAccess(Long userId, Long postId) {

        if (userId == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new CustomException(ErrorCode.POST_NOT_PUBLISHED);
        }

        // 정보글이 아니면 구매 불필요
        if (post.getAccessType() != PostAccessType.POINT_REQUIRED) {
            int bal = pointService.getBalance(userId);
            return new PurchasePostAccessResponse(postId, ContentAccessStatus.GRANTED, bal);
        }

        Integer cost = post.getRequiredPoints();
        if (cost == null || cost <= 0) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

        // 작성자는 무료
        if (userId.equals(post.getUserId())) {
            int bal = pointService.getBalance(userId);
            return new PurchasePostAccessResponse(postId, ContentAccessStatus.GRANTED, bal);
        }

        // 이미 구매했으면 멱등 처리
        if (postAccessRepository.existsByPost_IdAndUserId(postId, userId)) {
            int bal = pointService.getBalance(userId);
            return new PurchasePostAccessResponse(postId, ContentAccessStatus.GRANTED, bal);
        }

        // 포인트 차감 (eventKey로 멱등)
        pointService.spendPoint(userId, cost, PointEvent.postAccess(userId, postId));

        // 열람권 저장 (유니크키로 멱등)
        try {
            postAccessRepository.save(PostAccess.of(userId, post, cost));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // uk(user_id, post_id) 충돌이면 이미 저장된 것으로 간주
        }

        int remaining = pointService.getBalance(userId);
        return new PurchasePostAccessResponse(postId, ContentAccessStatus.GRANTED, remaining);
    }

    private PostStats getOrCreateStats(Posts post) {
        return postStatsRepository.findByPost_Id(post.getId())
                .orElseGet(() -> postStatsRepository.save(PostStats.init(post)));
    }

}
