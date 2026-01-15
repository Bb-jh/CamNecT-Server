package CamNecT.CamNecT_Server.global.tag.model;

public enum EducationStatus {
    ATTENDING("재학"),
    ON_LEAVE("휴학"),
    GRADUATED("졸업");

    private final String description;

    EducationStatus(String description) {
        this.description = description;
    }
}