package CamNecT.CamNecT_Server.global.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.s3.presign")
public record PresignProps(long uploadExpirationSeconds, long downloadExpirationSeconds) {}