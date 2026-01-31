package CamNecT.CamNecT_Server.domain.verification.document.controller;

import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationDetailResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationListItemResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.SubmitDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.dto.SubmitDocumentVerificationResponse;
import CamNecT.CamNecT_Server.domain.verification.document.service.DocumentVerificationService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignDownloadResponse;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification/documents")
public class DocumentVerificationController {

    private final DocumentVerificationService service;

    @PostMapping("/uploads/presign")
    public PresignUploadResponse presignUpload(
            @UserId Long userId,
            @RequestBody @Valid PresignUploadRequest req
    ) {
        return service.presignUpload(userId, req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmitDocumentVerificationResponse submit(
            @UserId Long userId,
            @RequestBody @Valid SubmitDocumentVerificationRequest req
    ) {
        return service.submit(userId, req.docType(), req.documentKey());
    }

    @GetMapping("/me")
    public List<DocumentVerificationListItemResponse> mySubmissions(@UserId Long userId) {
        return service.mySubmissions(userId);
    }

    @GetMapping("/{submissionId}")
    public DocumentVerificationDetailResponse mySubmissionDetail(
            @UserId Long userId,
            @PathVariable Long submissionId
    ) {
        return service.mySubmissionDetail(userId, submissionId);
    }

    @GetMapping("/{submissionId}/files/{fileId}/download-url")
    public PresignDownloadResponse myDownloadUrl(
            @UserId Long userId,
            @PathVariable Long submissionId,
            @PathVariable Long fileId
    ) {
        return service.myDownloadUrl(userId, submissionId, fileId);
    }

    @DeleteMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @UserId Long userId,
            @PathVariable Long submissionId
    ) {
        service.cancel(userId, submissionId);
    }
}
