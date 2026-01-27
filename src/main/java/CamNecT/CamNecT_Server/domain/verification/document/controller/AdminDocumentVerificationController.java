package CamNecT.CamNecT_Server.domain.verification.document.controller;

import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationSubmission;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.domain.verification.document.service.AdminDocumentVerificationService;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/verification/documents")
public class AdminDocumentVerificationController {

    private final AdminDocumentVerificationService service;

    @GetMapping
    public Page<DocumentVerificationSubmission> list(
            @RequestParam(defaultValue = "PENDING") VerificationStatus status,
            Pageable pageable
    ) {
        return service.list(status, pageable);
    }

    @GetMapping("/{submissionId}")
    public DocumentVerificationSubmission get(@PathVariable Long submissionId) {
        return service.get(submissionId);
    }

    @PatchMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void review(
            @RequestParam Long adminId,
            @PathVariable Long submissionId,
            @RequestBody @Validated ReviewDocumentVerificationRequest req
    ) {
        service.review(adminId, submissionId, req);
    }

    @GetMapping("/{submissionId}/files/{fileId}/download-url")
    public PresignDownloadResponse downloadUrl(
            @PathVariable Long submissionId,
            @PathVariable Long fileId
    ) {
        return service.downloadUrl(submissionId, fileId);
    }
}
