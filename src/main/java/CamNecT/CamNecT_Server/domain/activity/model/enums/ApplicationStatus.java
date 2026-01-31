package CamNecT.CamNecT_Server.domain.activity.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
    REQUESTED("requested"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String value;
}