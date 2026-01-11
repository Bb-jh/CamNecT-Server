package CamNecT.CamNecT_Server.domain.auth.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequest (
    @Email @NotBlank String email,
    @NotBlank String username, //실질적 아이디
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String phoneNum,
    @NotNull Agreements agreements
){
    public record Agreements(
            boolean serviceTerms,
            boolean privacyTerms
    ) {}
}
