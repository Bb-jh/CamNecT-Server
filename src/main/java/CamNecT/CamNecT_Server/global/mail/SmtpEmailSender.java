package CamNecT.CamNecT_Server.global.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${app.auth.email-verification.mail.subject:CamNecT 이메일 인증 링크}")
    private String subject;

    @Override
    public void sendEmailVerificationCode(String toEmail, String code, long expiresMinutes) {
        try {
            var mimeMessage = mailSender.createMimeMessage();

            var helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(toEmail);

            if (StringUtils.hasText(from)) {
                helper.setFrom(from);
            }

            helper.setSubject(subject);

            String text = """
                CamNecT 이메일 인증번호입니다.

                인증번호: %s

                유효시간: %d분
                """.formatted(code, expiresMinutes);

            String html = """
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                  <h2>이메일 인증번호</h2>
                  <p>아래 <b>6자리 인증번호</b>를 사이트에 입력해 인증을 완료해 주세요.</p>

                  <div style="margin: 16px 0; padding: 14px; border: 1px solid #ddd; border-radius: 8px;">
                    <div style="font-size: 14px; color: #555;">인증번호</div>
                    <div style="font-size: 28px; letter-spacing: 6px; font-weight: 700;">%s</div>
                  </div>

                  <p style="font-size: 12px; color: #777;">
                    유효시간: %d분
                  </p>
                </div>
                """.formatted(code, expiresMinutes);

            // 보안상 code는 로그에 남기지 않는 편이 좋습니다.
            log.info("[mail] send verification code to={}", toEmail);
            helper.setText(text, html); //html 우선. html이 안되는 환경이면 text
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new IllegalStateException("MAIL_SEND_FAILED", e);
        }
    }
}
