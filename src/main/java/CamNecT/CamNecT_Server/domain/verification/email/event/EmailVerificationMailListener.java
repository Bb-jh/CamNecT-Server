package CamNecT.CamNecT_Server.domain.verification.email.event;

import CamNecT.CamNecT_Server.global.mail.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailVerificationMailListener {

    private final EmailSender emailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(EmailVerificationCodeIssuedEvent e) {
        emailSender.sendEmailVerificationCode(e.email(), e.code(), e.expiresMinutes());
    }
}
