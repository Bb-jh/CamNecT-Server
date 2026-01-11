package CamNecT.CamNecT_Server.domain.verification.email.controller;

import CamNecT.CamNecT_Server.domain.verification.email.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/email/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verify(@RequestParam("token") String token) {
        emailVerificationService.verifyEmail(token);
    }

    //TODO : email 재인증(토큰 재발급)등 api 추가 필요성 보임.

    //관리자가 직접 재학증명서 인증하는 로직은 .domain.verification에서 처리
}
