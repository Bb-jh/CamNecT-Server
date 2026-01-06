package CamNecT.CamNecT_Server.global.common.mail;

public interface EmailSender {
    void sendEmailVerification(String toEmail, String verifyUrl);
}
