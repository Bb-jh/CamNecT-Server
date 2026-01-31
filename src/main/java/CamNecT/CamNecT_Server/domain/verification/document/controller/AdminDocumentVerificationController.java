package CamNecT.CamNecT_Server.domain.verification.document.controller;

import CamNecT.CamNecT_Server.domain.verification.document.dto.AdminDocumentVerificationDetailResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.AdminDocumentVerificationListItemResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.AdminReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.domain.verification.document.service.AdminDocumentVerificationService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignDownloadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/verification/documents")
public class AdminDocumentVerificationController {

    private final AdminDocumentVerificationService service;

    @GetMapping
    public Page<AdminDocumentVerificationListItemResponse> list(
            @RequestParam(defaultValue = "PENDING") VerificationStatus status,
            Pageable pageable
    ) {
        return service.list(status, pageable);
    }

    @GetMapping("/{submissionId}")
    public AdminDocumentVerificationDetailResponse get(@PathVariable Long submissionId) {
        return service.get(submissionId);
    }

    @PatchMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void review(
            @UserId Long adminId,
            @PathVariable Long submissionId,
            @RequestBody @Valid AdminReviewDocumentVerificationRequest req
    ) {
        service.review(adminId, submissionId, req);
    }

    @GetMapping("/{submissionId}/download-url")
    public PresignDownloadResponse downloadUrl(@PathVariable Long submissionId) {
        return service.downloadUrl(submissionId);
    }
}
