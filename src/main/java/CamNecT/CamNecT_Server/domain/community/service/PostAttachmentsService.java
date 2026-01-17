package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.dto.request.AttachmentRequest;
import CamNecT.CamNecT_Server.domain.community.model.Posts.PostAttachments;
import CamNecT.CamNecT_Server.domain.community.model.Posts.Posts;
import CamNecT.CamNecT_Server.domain.community.repository.Posts.PostAttachmentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostAttachmentsService {

    private final PostAttachmentsRepository postAttachmentsRepository;

    /**
     * 첨부 "교체" 전략:
     * 1) 기존 active 첨부 soft delete
     * 2) 새 첨부를 sortOrder(0..n-1)로 저장
     */
    @Transactional
    public void replace(Posts post, List<AttachmentRequest> attachments) {
        postAttachmentsRepository.softDeleteByPostId(post.getId());

        if (attachments == null || attachments.isEmpty()) return;

        List<PostAttachments> toSave = new ArrayList<>();
        int order = 0;

        for (AttachmentRequest req : attachments) {
            if (req == null) continue;
            if (req.fileUrl() == null || req.fileUrl().isBlank()) continue;

            toSave.add(PostAttachments.create(
                    post,
                    req.fileUrl(),
                    req.thumbnailUrl(),
                    req.width(),
                    req.height(),
                    req.fileSize(),
                    order++
            ));
        }

        if (!toSave.isEmpty()) {
            postAttachmentsRepository.saveAll(toSave);
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
