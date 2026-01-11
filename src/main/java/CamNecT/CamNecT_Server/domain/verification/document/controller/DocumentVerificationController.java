package CamNecT.CamNecT_Server.domain.verification.document.controller;

import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationDetailResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.DocumentVerificationListItemResponse;
import CamNecT.CamNecT_Server.domain.verification.document.dto.SubmitDocumentVerificationResponse;
import CamNecT.CamNecT_Server.domain.verification.document.model.DocumentType;
import CamNecT.CamNecT_Server.domain.verification.document.service.DocumentVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification/documents")
public class DocumentVerificationController {

    private final DocumentVerificationService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SubmitDocumentVerificationResponse submit(
            // TODO: @LoginUser Long userId 로 교체 권장
            @RequestParam Long userId,
            @RequestParam DocumentType docType,
            @RequestPart("documents") List<MultipartFile> documents
    ) {
        return service.submit(userId, docType, documents);
    }

    @GetMapping("/me")
    public List<DocumentVerificationListItemResponse> mySubmissions(
            @RequestParam Long userId
    ) {
        return service.mySubmissions(userId);
    }

    @GetMapping("/{submissionId}")
    public DocumentVerificationDetailResponse mySubmissionDetail(
            @RequestParam Long userId,
            @PathVariable Long submissionId
    ) {
        return service.mySubmissionDetail(userId, submissionId);
    }

    @DeleteMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @RequestParam Long userId,
            @PathVariable Long submissionId
    ) {
        service.cancel(userId, submissionId);
    }
}
