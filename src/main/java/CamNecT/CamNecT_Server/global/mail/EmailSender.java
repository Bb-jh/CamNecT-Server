package CamNecT.CamNecT_Server.global.mail;

public interface EmailSender {
    void sendEmailVerification(String toEmail, String verifyUrl);
}
