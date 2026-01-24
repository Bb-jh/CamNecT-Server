package CamNecT.CamNecT_Server.global.mail;

public interface EmailSender {
    void sendEmailVerificationCode(String toEmail, String code, long expiresMinutes);
}
