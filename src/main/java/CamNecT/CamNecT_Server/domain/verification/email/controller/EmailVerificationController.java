package CamNecT.CamNecT_Server.domain.verification.email.controller;

import CamNecT.CamNecT_Server.domain.verification.email.dto.VerifyEmailCodeRequest;
import CamNecT.CamNecT_Server.domain.verification.email.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/email/verify-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyCode(@RequestBody @Valid VerifyEmailCodeRequest req) {
        emailVerificationService.verifyEmailCode(req.userId(), req.code());
    }
}
