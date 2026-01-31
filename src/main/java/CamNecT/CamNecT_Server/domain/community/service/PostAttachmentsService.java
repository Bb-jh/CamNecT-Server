package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.AttachmentRequest;
import CamNecT.CamNecT_Server.domain.community.model.Posts.CommunityAttachmentProps;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAttachments;
import CamNecT.CamNecT_Server.domain.community.model.Posts.Posts;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostAttachmentsRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.CommunityErrorCode;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.StorageErrorCode;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadRefType;
import CamNecT.CamNecT_Server.global.storage.model.UploadTicket;
import CamNecT.CamNecT_Server.global.storage.repository.UploadTicketRepository;
import CamNecT.CamNecT_Server.global.storage.service.FileStorage;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostAttachmentsService {

    private final PostAttachmentsRepository postAttachmentsRepository;
    private final FileStorage fileStorage;

    private final PresignEngine presignEngine;
    private final CommunityAttachmentProps props;

    private final UploadTicketRepository ticketRepo;

    @Transactional
    public PresignUploadResponse presign(Long userId, PresignUploadRequest req) {
        String ct = normalize(req.contentType());

        if (req.size() <= 0) throw new CustomException(StorageErrorCode.EMPTY_FILE_NOT_ALLOWED);
        if (req.size() > props.maxFileSizeBytes()) throw new CustomException(StorageErrorCode.FILE_TOO_LARGE);
        if (!StringUtils.hasText(ct) || !props.allowedContentTypes().contains(ct)) {
            throw new CustomException(StorageErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }

        long pending = ticketRepo.countByUserIdAndPurposeAndStatus(
                userId, UploadPurpose.COMMUNITY_POST_ATTACHMENT, UploadTicket.Status.PENDING
        );
        if (pending >= props.maxFiles()) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_LIMIT_EXCEEDED);
        }

        String tempPrefix = "community/temp/user-" + userId + "/attachments";

        return presignEngine.issueUpload(
                userId,
                UploadPurpose.COMMUNITY_POST_ATTACHMENT,
                tempPrefix,
                ct,
                req.size(),
                req.originalFilename()
        );
    }



    @Transactional
    public void replace(Posts post, Long userId, List<AttachmentRequest> attachments) {

        if (attachments != null && attachments.size() > props.maxFiles()) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_LIMIT_EXCEEDED);
        }

        List<PostAttachments> oldActive =
                postAttachmentsRepository.findByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(post.getId());

        Set<String> oldKeys = new HashSet<>();
        for (PostAttachments a : oldActive) {
            if (StringUtils.hasText(a.getFileKey())) oldKeys.add(a.getFileKey());
            if (StringUtils.hasText(a.getThumbnailKey())) oldKeys.add(a.getThumbnailKey());
        }

        postAttachmentsRepository.softDeleteByPostId(post.getId());

        if (attachments == null || attachments.isEmpty()) {
            registerAfterCommitDelete(oldActive, Set.of());
            return;
        }

        String finalFilePrefix = "community/posts/post-" + post.getId() + "/attachments";
        String finalThumbPrefix = "community/posts/post-" + post.getId() + "/thumbnails";

        List<PostAttachments> toSave = new ArrayList<>();
        int order = 0;

        Set<String> newFinalKeys = new HashSet<>();
        Set<String> consumedThisRequest = new HashSet<>();

        for (AttachmentRequest req : attachments) {
            if (req == null) continue;

            String inFileKey = req.fileKey();
            if (!StringUtils.hasText(inFileKey)) continue;

            String inThumbKey = req.thumbnailKey();

            String finalFileKey = resolveFinalKey(
                    userId, post.getId(), oldKeys, consumedThisRequest,
                    inFileKey, finalFilePrefix
            );

            String finalThumbKey = null;
            if (StringUtils.hasText(inThumbKey)) {
                finalThumbKey = resolveFinalKey(
                        userId, post.getId(), oldKeys, consumedThisRequest,
                        inThumbKey, finalThumbPrefix
                );
            }

            newFinalKeys.add(finalFileKey);
            if (StringUtils.hasText(finalThumbKey)) newFinalKeys.add(finalThumbKey);

            toSave.add(PostAttachments.create(
                    post,
                    finalFileKey,
                    finalThumbKey,
                    req.width(),
                    req.height(),
                    req.fileSize(),
                    order++
            ));
        }

        if (!toSave.isEmpty()) {
            postAttachmentsRepository.saveAll(toSave);
        }

        registerAfterCommitDelete(oldActive, newFinalKeys);
    }

    private String resolveFinalKey(
            Long userId,
            Long postId,
            Set<String> oldKeys,
            Set<String> consumedThisRequest,
            String keyFromClient,
            String finalPrefix
    ) {
        if (oldKeys.contains(keyFromClient)) {
            return keyFromClient;
        }

        if (!consumedThisRequest.add(keyFromClient)) {
            return keyFromClient;
        }

        return presignEngine.consume(
                userId,
                UploadPurpose.COMMUNITY_POST_ATTACHMENT,
                UploadRefType.POST,
                postId,
                keyFromClient,
                finalPrefix
        );
    }

    @Transactional
    public void purgeAllByPostId(Long postId) {
        List<PostAttachments> oldActive =
                postAttachmentsRepository.findByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(postId);

        postAttachmentsRepository.softDeleteByPostId(postId);

        registerAfterCommitDelete(oldActive, Set.of());
    }

    private void registerAfterCommitDelete(List<PostAttachments> oldActive, Set<String> newKeys) {
        Set<String> deleteKeys = new HashSet<>();

        for (PostAttachments a : oldActive) {
            String fk = a.getFileKey();
            String tk = a.getThumbnailKey();

            if (StringUtils.hasText(fk) && !newKeys.contains(fk)) deleteKeys.add(fk);
            if (StringUtils.hasText(tk) && !newKeys.contains(tk)) deleteKeys.add(tk);
        }

        if (deleteKeys.isEmpty()) return;

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (String key : deleteKeys) {
                        try { fileStorage.delete(key); } catch (Exception ignored) {}
                    }
                }
            });
        } else {
            for (String key : deleteKeys) {
                try { fileStorage.delete(key); } catch (Exception ignored) {}
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PostAttachments> findActive(Long postId) {
        return postAttachmentsRepository.findByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(postId);
    }

    @Transactional(readOnly = true)
    public PostAttachments findActiveOne(Long postId, Long attachmentId) {
        return postAttachmentsRepository.findByIdAndPost_IdAndStatusTrue(attachmentId, postId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.ATTACHMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<PostAttachments> findActiveByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return List.of();
        return postAttachmentsRepository.findActiveByPostIds(postIds);
    }

    private String normalize(String ct) {
        return (ct == null) ? "" : ct.trim().toLowerCase(Locale.ROOT);
    }
}
