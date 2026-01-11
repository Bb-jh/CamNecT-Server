package CamNecT.CamNecT_Server.domain.auth.controller;

import CamNecT.CamNecT_Server.domain.auth.dto.login.LoginRequest;
import CamNecT.CamNecT_Server.domain.auth.dto.login.LoginResponse;
import CamNecT.CamNecT_Server.domain.auth.dto.signup.SignupRequest;
import CamNecT.CamNecT_Server.domain.auth.dto.signup.SignupResponse;
import CamNecT.CamNecT_Server.domain.auth.service.LoginService;
import CamNecT.CamNecT_Server.domain.auth.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final SignupService signupService;
    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return loginService.login(req);
    }

    /*
    * 아래는 회원가입 관련 API입니다.
    * 구현 : POST:/signup=회원가입(정보입력), GET:/email/verify=이메일인증전송,
    * 미구현 : GET:/email/resend=이메일재전송(토큰만료등 상황시),
    * 추가로 약관동의등에서 약관을 서버가 가지고 있는다면
    * GET: /terms, POST: /terms/agree 등 추가 가능성 있음.
    * */

    /*TODO : 약관동의 정보를 서버에서 갖고 있을 것인가?
    그렇다면 /terms/* API가 필요하다.
     */

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@RequestBody @Valid SignupRequest req) {
        return signupService.signup(req);
    }

    @GetMapping("/email/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verify(@RequestParam("token") String token) {
        signupService.verifyEmail(token);
    }

    //TODO : email 재인증(토큰 재발급)등 api 추가 필요성 보임.

}