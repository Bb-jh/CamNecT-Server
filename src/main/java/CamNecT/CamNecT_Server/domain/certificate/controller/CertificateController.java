package CamNecT.CamNecT_Server.domain.certificate.controller;

import CamNecT.CamNecT_Server.domain.certificate.dto.request.CertificateRequest;
import CamNecT.CamNecT_Server.domain.certificate.dto.response.CertificateResponse;
import CamNecT.CamNecT_Server.domain.certificate.service.CertificateService;
import CamNecT.CamNecT_Server.domain.users.model.CustomUserDetails;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/me/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    // 학력 조회
    @GetMapping
    public ApiResponse<List<CertificateResponse>> getMyCertificates(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CertificateResponse> response = certificateService.getMyCertificate(userDetails.getUserId());
        return ApiResponse.success(response);
    }

    // 학력 추가
    @PostMapping
    public ApiResponse<Void> addCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CertificateRequest request
    ) {
        certificateService.addCertificate(userDetails.getUserId(), request);
        return ApiResponse.success(null);
    }

    // 학력 수정
    @PatchMapping("/{certificateId}")
    public ApiResponse<Void> updateCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long certificateId,
            @RequestBody @Valid CertificateRequest request
    ) {
        certificateService.updateCertificate(userDetails.getUserId(), certificateId, request);
        return ApiResponse.success(null);
    }

    // 학력 삭제
    @DeleteMapping("/{certificateId}")
    public ApiResponse<Void> deleteCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long certificateId
    ) {
        certificateService.deleteCertificate(userDetails.getUserId(), certificateId);
        return ApiResponse.success(null);
    }
}