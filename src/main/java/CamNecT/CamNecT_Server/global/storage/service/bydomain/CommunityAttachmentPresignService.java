package CamNecT.CamNecT_Server.global.storage.service.bydomain;

import CamNecT.CamNecT_Server.domain.community.model.Posts.CommunityAttachmentProps;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.StorageErrorCode;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadTicket;
import CamNecT.CamNecT_Server.global.storage.repository.UploadTicketRepository;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityAttachmentPresignService {

    private final CommunityAttachmentProps props;
    private final UploadTicketRepository ticketRepo;
    private final PresignEngine presignEngine;

    @Transactional
    public PresignUploadResponse presign(Long userId, PresignUploadRequest req) {
        String ct = req.contentType();
        long size = req.size();

        if (size <= 0) throw new CustomException(StorageErrorCode.STORAGE_EMPTY_FILE);
        if (size > props.maxFileSizeBytes()) throw new CustomException(StorageErrorCode.FILE_TOO_LARGE);
        if (!props.allowedContentTypes().contains(ct)) throw new CustomException(StorageErrorCode.UNSUPPORTED_CONTENT_TYPE);

        // 게시글당 3개 제한을 “발급 단계”에서 1차로 막기 (abuse 방지)
        long pending = ticketRepo.countByUserIdAndPurposeAndStatus(
                userId, UploadPurpose.COMMUNITY_POST_ATTACHMENT, UploadTicket.Status.PENDING
        );
        if (pending >= props.maxFiles()) {
            throw new CustomException(StorageErrorCode.UPLOAD_TICKET_LIMIT_EXCEEDED);
        }

        String prefix = "community/attachments/user-" + userId;

        return presignEngine.issueUpload(
                userId,
                UploadPurpose.COMMUNITY_POST_ATTACHMENT,
                prefix,
                ct,
                size,
                req.originalFilename()
        );
    }
}