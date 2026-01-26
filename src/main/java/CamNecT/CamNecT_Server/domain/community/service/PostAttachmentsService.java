package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.AttachmentRequest;
import CamNecT.CamNecT_Server.domain.community.model.Posts.CommunityAttachmentProps;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAttachments;
import CamNecT.CamNecT_Server.domain.community.model.Posts.Posts;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostAttachmentsRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.StorageErrorCode;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadRefType;
import CamNecT.CamNecT_Server.global.storage.service.FileStorage;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostAttachmentsService {

    private final PostAttachmentsRepository postAttachmentsRepository;
    private final FileStorage fileStorage;

    private final PresignEngine presignEngine;
    private final CommunityAttachmentProps props;

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

        List<PostAttachments> toSave = new ArrayList<>();
        int order = 0;

        Set<String> newKeys = new HashSet<>();
        Set<String> consumedThisRequest = new HashSet<>();

        for (AttachmentRequest req : attachments) {
            if (req == null) continue;

            String fileKey = req.fileKey();
            if (!StringUtils.hasText(fileKey)) continue;

            String thumbKey = req.thumbnailKey();

            newKeys.add(fileKey);
            if (StringUtils.hasText(thumbKey)) newKeys.add(thumbKey);

            if (!oldKeys.contains(fileKey) && consumedThisRequest.add(fileKey)) {
                presignEngine.consume(
                        userId,
                        UploadPurpose.COMMUNITY_POST_ATTACHMENT,
                        UploadRefType.POST,
                        post.getId(),
                        fileKey
                );
            }
            if (StringUtils.hasText(thumbKey) && !oldKeys.contains(thumbKey) && consumedThisRequest.add(thumbKey)) {
                presignEngine.consume(
                        userId,
                        UploadPurpose.COMMUNITY_POST_ATTACHMENT,
                        UploadRefType.POST,
                        post.getId(),
                        thumbKey
                );
            }

            toSave.add(PostAttachments.create(
                    post,
                    fileKey,
                    thumbKey,
                    req.width(),
                    req.height(),
                    req.fileSize(),
                    order++
            ));
        }

        if (!toSave.isEmpty()) {
            postAttachmentsRepository.saveAll(toSave);
        }

        registerAfterCommitDelete(oldActive, newKeys);
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
    public PostAttachments findThumbnailOrNull(Long postId) {
        return postAttachmentsRepository.findTop1ByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(postId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PostAttachments> findActiveByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return List.of();
        return postAttachmentsRepository.findActiveByPostIds(postIds);
    }
}
