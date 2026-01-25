package CamNecT.CamNecT_Server.global.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.local")
public record LocalStorageProps(String baseDir) {}