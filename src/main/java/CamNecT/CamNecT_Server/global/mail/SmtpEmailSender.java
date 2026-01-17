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
    public void sendEmailVerification(String toEmail, String verifyUrl) {
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

            //TODO : 메일본문 넣어야됨
            String text = """
                아래 링크를 클릭하면 이메일 인증이 완료됩니다.

                %s
                """.formatted(verifyUrl);

            //TODO : 여기 html만들어서 삽입해야됨.
            String html = """
                <div>...%s...</div>
                """.formatted(verifyUrl);

            log.info("[mail] send verification to={} url={}", toEmail, verifyUrl);
            helper.setText(text, html); //html 우선. html이 안되는 환경이면 text
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new IllegalStateException("MAIL_SEND_FAILED", e);
        }
    }
}
