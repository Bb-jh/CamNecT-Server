package CamNecT.CamNecT_Server.domain.verification.document.controller;

import CamNecT.CamNecT_Server.domain.verification.document.dto.ReviewDocumentVerificationRequest;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentVerificationSubmission;
import CamNecT.CamNecT_Server.domain.verification.document.model.VerificationStatus;
import CamNecT.CamNecT_Server.domain.verification.document.service.AdminDocumentVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            // TODO: 관리자 인증 붙이면 @LoginUser adminId + Role 체크
            @RequestParam Long adminId,
            @PathVariable Long submissionId,
            @RequestBody @Validated ReviewDocumentVerificationRequest req
    ) {
        service.review(adminId, submissionId, req);
    }

    @GetMapping("/{submissionId}/files/{fileId}")
    public ResponseEntity<Resource> download(
            @PathVariable Long submissionId,
            @PathVariable Long fileId
    ) {
        var result = service.downloadFileWithMeta(submissionId, fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(result.originalFilename()))
                .contentType(MediaType.parseMediaType(result.contentType()))
                .contentLength(result.size())
                .body(result.resource());
    }

    private String contentDisposition(String filename) {
        // filename에 특수문자/공백이 있을 수 있으니 RFC 5987 형태도 같이 주는 방식
        String safe = (filename == null || filename.isBlank()) ? "document" : filename.replace("\"", "");
        return "attachment; filename=\"" + safe + "\"";
    }
}
