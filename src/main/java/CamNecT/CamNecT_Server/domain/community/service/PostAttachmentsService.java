package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.AttachmentRequest;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAttachments;
import CamNecT.CamNecT_Server.domain.community.model.Posts.Posts;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostAttachmentsRepository;
import CamNecT.CamNecT_Server.global.storage.service.FileStorage;
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

    /**
     * 첨부 "교체" 전략:
     * 1) 기존 active 첨부 조회
     * 2) 기존 active 첨부 soft delete
     * 3) 새 첨부를 sortOrder(0..n-1)로 저장
     * 4) (커밋 후) 새 목록에 없는 기존 파일들은 storage에서 delete
     */
    @Transactional
    public void replace(Posts post, List<AttachmentRequest> attachments) {
        // 1) 기존 active 조회 (삭제 후보 계산용)
        List<PostAttachments> oldActive =
                postAttachmentsRepository.findByPost_IdAndStatusTrueOrderBySortOrderAscIdAsc(post.getId());

        // 2) 기존 soft delete
        postAttachmentsRepository.softDeleteByPostId(post.getId());

        // 3) 새 첨부 저장 준비
        if (attachments == null || attachments.isEmpty()) {
            // 새 목록이 비었다면: 기존 파일들 전부 삭제 후보 (커밋 후 삭제)
            registerAfterCommitDelete(oldActive, Set.of());
            return;
        }

        List<PostAttachments> toSave = new ArrayList<>();
        int order = 0;

        Set<String> newKeys = new HashSet<>();

        for (AttachmentRequest req : attachments) {
            if (req == null) continue;

            String fileKey = req.fileKey();
            if (!StringUtils.hasText(fileKey)) continue;

            String thumbKey = req.thumbnailKey();

            newKeys.add(fileKey);
            if (StringUtils.hasText(thumbKey)) newKeys.add(thumbKey);

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

    private void registerAfterCommitDelete(List<PostAttachments> oldActive, Set<String> newKeys) {
        // old에서 삭제 후보 key 모으기
        Set<String> deleteKeys = new HashSet<>();

        for (PostAttachments a : oldActive) {
            String fk = a.getFileKey();
            String tk = a.getThumbnailKey();

            if (StringUtils.hasText(fk) && !newKeys.contains(fk)) deleteKeys.add(fk);
            if (StringUtils.hasText(tk) && !newKeys.contains(tk)) deleteKeys.add(tk);
        }

        if (deleteKeys.isEmpty()) return;

        // 트랜잭션이 있는 경우에만 afterCommit 등록
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (String key : deleteKeys) {
                        try {
                            fileStorage.delete(key); // S3/local 자동
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        } else {
            // 트랜잭션이 아닌 호출이면 즉시 삭제
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
