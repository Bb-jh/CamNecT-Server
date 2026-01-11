package CamNecT.CamNecT_Server.domain.verification.document.dto;

import org.springframework.core.io.Resource;

public record DownloadResult(Resource resource, String originalFilename, String contentType, long size) {}
