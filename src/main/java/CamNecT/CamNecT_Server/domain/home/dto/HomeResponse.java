package CamNecT.CamNecT_Server.domain.home.dto;

public record HomeResponse(
        UserSection user,
        NotificationSection notification
//        CoffeeChatSection coffeeChat,
//        ScheduleSection schedule,
//        PointSection point,
//        RecommendedAlumniSection recommendedAlumni,
//        FeaturedContestsSection featuredContests
) {
    public static HomeResponse of(String displayName, long unreadCount) {
        return new HomeResponse(
                UserSection.of(displayName),
                NotificationSection.of(unreadCount)
//                CoffeeChatSection.empty(),
//                ScheduleSection.empty(),
//                new PointSection(0),
//                RecommendedAlumniSection.empty(),
//                FeaturedContestsSection.empty()
        );
    }

    public record UserSection(String displayName) {
        public static UserSection of(String displayName) {
            return new UserSection(displayName);
        }
    }

    public record NotificationSection(long unreadCount, boolean hasUnread) {
        public static NotificationSection of(long unreadCount) {
            return new NotificationSection(unreadCount, unreadCount > 0);
        }
    }
}
