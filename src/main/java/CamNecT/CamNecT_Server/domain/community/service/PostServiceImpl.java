package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.CreatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.request.UpdatePostRequest;
import CamNecT.CamNecT_Server.domain.community.dto.response.CreatePostResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.PostDetailResponse;
import CamNecT.CamNecT_Server.domain.community.dto.response.ToggleLikeResponse;
import CamNecT.CamNecT_Server.domain.community.event.CommentAcceptedEvent;
import CamNecT.CamNecT_Server.domain.community.model.*;
import CamNecT.CamNecT_Server.domain.community.model.Comments.AcceptedComments;
import CamNecT.CamNecT_Server.domain.community.model.Comments.Comments;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostLikes;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostStats;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostTags;
import CamNecT.CamNecT_Server.domain.community.model.Posts.Posts;
import CamNecT.CamNecT_Server.domain.community.model.enums.BoardCode;
import CamNecT.CamNecT_Server.domain.community.model.enums.PostStatus;
import CamNecT.CamNecT_Server.domain.community.repository.*;
import CamNecT.CamNecT_Server.domain.community.repository.Comments.AcceptedCommentsRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Comments.CommentsRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostLikesRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostStatsRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostTagsRepository;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostsRepository;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public CreatePostResponse create(Long userId, CreatePostRequest req) {
        // TODO: 인증 붙이면 userId를 SecurityContext에서 꺼내도록 변경
        if (userId == null) userId = 1L;

        Boards board = boardsRepository.findByCode(req.boardCode())
                .orElseThrow(() -> new IllegalArgumentException("board not found: " + req.boardCode()));

        Posts post = Posts.create(board, userId, req.title(), req.content(), Boolean.TRUE.equals(req.anonymous()));
        Posts saved = postsRepository.save(post);

        // stats 생성(필수)
        postStatsRepository.save(PostStats.init(saved));

        // 태그 연결
        replaceTags(saved, req.tagIds());

        // 첨부는 PostAttachmentsService로 분리하거나, 기존 로직이 있으면 여기서 호출
        return new CreatePostResponse(saved.getId());
    }

    @Transactional
    @Override
    public void update(Long userId, Long postId, UpdatePostRequest req) {
        if (userId == null) userId = 1L;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));

        if (!Objects.equals(post.getUserId(), userId)) {
            throw new IllegalArgumentException("forbidden");
        }

        post.update(req.title(), req.content(), req.anonymous());

        // 태그는 “요청이 들어온 경우”만 교체
        if (req.tagIds() != null) {
            postTagsRepository.deleteByPost_Id(postId);
            replaceTags(post, req.tagIds());
        }

        touchStats(postId);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long postId) {
        if (userId == null) userId = 1L;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));

        if (!Objects.equals(post.getUserId(), userId)) {
            throw new IllegalArgumentException("forbidden");
        }

        post.deleteSoft();

        // 연관 테이블 정리(필요한 것만)
        postTagsRepository.deleteByPost_Id(postId);
        acceptedCommentsRepository.deleteByPost_Id(postId);
    }

    @Transactional
    @Override
    public ToggleLikeResponse toggleLike(Long userId, Long postId) {
        if (userId == null) userId = 1L;

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));

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
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new IllegalArgumentException("post not published");
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

        return new PostDetailResponse(
                post.getId(),
                post.getBoard().getCode(),
                post.getTitle(),
                post.getContent(),
                post.isAnonymous(),
                post.getUserId(),
                stats.getViewCount(),
                stats.getLikeCount(),
                likedByMe,
                acceptedCommentId,
                tagIds
        );
    }

    @Transactional
    @Override
    public void acceptComment(Long userId, Long postId, Long commentId) {
        if (userId == null) userId = 1L;


        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));
        if (post.getBoard().getCode() != BoardCode.QUESTION) {
            throw new IllegalArgumentException("only question board can accept");
        }
        // 질문 작성자만 채택 가능
        if (!Objects.equals(post.getUserId(), userId)) {
            throw new IllegalArgumentException("forbidden");
        }

        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found: " + commentId));

        if (!Objects.equals(comment.getPost().getId(), postId)) {
            throw new IllegalArgumentException("comment not in post");
        }

        if (acceptedCommentsRepository.existsByPost_Id(postId)) {
            throw new IllegalArgumentException("already accepted");
        }

        acceptedCommentsRepository.save(AcceptedComments.of(post, comment, userId));
        touchStats(postId);

        Long receiverId = comment.getUserId(); // 프로젝트에 맞게 comment.getUser().getId()일 수도 있음
        if (receiverId != null && !Objects.equals(receiverId, userId)) {
            eventPublisher.publishEvent(new CommentAcceptedEvent(
                    receiverId, postId, commentId, userId
            ));
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
            throw new IllegalArgumentException("invalid tagIds");
        }

        for (Tag t : tags) {
            if (!t.isActive()) throw new IllegalArgumentException("inactive tagId=" + t.getId());
            postTagsRepository.save(PostTags.link(post, t));
        }
    }

    private void touchStats(Long postId) {
        postStatsRepository.findByPost_Id(postId).ifPresent(PostStats::touch);
    }

    private PostStats getOrCreateStats(Posts post) {
        return postStatsRepository.findByPost_Id(post.getId())
                .orElseGet(() -> postStatsRepository.save(PostStats.init(post)));
    }
}
