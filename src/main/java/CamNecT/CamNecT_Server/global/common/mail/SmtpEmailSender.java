package CamNecT.CamNecT_Server.global.common.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Override
    public void sendEmailVerification(String toEmail, String verifyUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        if (from != null && !from.isBlank()) message.setFrom(from);

        message.setSubject("CamNecT 이메일 인증 링크");
        message.setText("아래 링크를 클릭하면 이메일 인증이 완료됩니다.\n\n"+verifyUrl);
        mailSender.send(message);
    }
}
