package CamNecT.CamNecT_Server.domain.home.dto;

public record HomeResponse(
//        UserSection user,
//        NotificationSection notification,
//        CoffeeChatSection coffeeChat,
//        ScheduleSection schedule,
//        PointSection point,
//        RecommendedAlumniSection recommendedAlumni,
//        FeaturedContestsSection featuredContests
) {
    public static HomeResponse empty(String displayName) {
        return new HomeResponse(
//                UserSection.of(displayName),
//                new NotificationSection(0),
//                CoffeeChatSection.empty(),
//                ScheduleSection.empty(),
//                new PointSection(0),
//                RecommendedAlumniSection.empty(),
//                FeaturedContestsSection.empty()
        );
    }
}
