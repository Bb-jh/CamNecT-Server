package CamNecT.CamNecT_Server.domain.activity.model.external_activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExternalActivity is a Querydsl query type for ExternalActivity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExternalActivity extends EntityPathBase<ExternalActivity> {

    private static final long serialVersionUID = -1028871669L;

    public static final QExternalActivity externalActivity = new QExternalActivity("externalActivity");

    public final NumberPath<Long> activityId = createNumber("activityId", Long.class);

    public final DatePath<java.time.LocalDate> applyEndDate = createDate("applyEndDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> applyStartDate = createDate("applyStartDate", java.time.LocalDate.class);

    public final EnumPath<CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityCategory> category = createEnum("category", CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityCategory.class);

    public final StringPath context = createString("context");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath officialUrl = createString("officialUrl");

    public final StringPath organizer = createString("organizer");

    public final StringPath region = createString("region");

    public final DatePath<java.time.LocalDate> resultAnnounceDate = createDate("resultAnnounceDate", java.time.LocalDate.class);

    public final EnumPath<CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityStatus> status = createEnum("status", CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityStatus.class);

    public final StringPath targetDescription = createString("targetDescription");

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QExternalActivity(String variable) {
        super(ExternalActivity.class, forVariable(variable));
    }

    public QExternalActivity(Path<? extends ExternalActivity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExternalActivity(PathMetadata metadata) {
        super(ExternalActivity.class, metadata);
    }

}

