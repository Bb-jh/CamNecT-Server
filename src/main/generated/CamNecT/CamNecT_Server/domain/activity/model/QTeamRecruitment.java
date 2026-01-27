package CamNecT.CamNecT_Server.domain.activity.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamRecruitment is a Querydsl query type for TeamRecruitment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamRecruitment extends EntityPathBase<TeamRecruitment> {

    private static final long serialVersionUID = 242003001L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamRecruitment teamRecruitment = new QTeamRecruitment("teamRecruitment");

    public final NumberPath<Integer> commentCount = createNumber("commentCount", Integer.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QExternalActivity externalActivity;

    public final NumberPath<Integer> recruitCount = createNumber("recruitCount", Integer.class);

    public final DatePath<java.time.LocalDate> recruitDeadline = createDate("recruitDeadline", java.time.LocalDate.class);

    public final NumberPath<Long> recruitId = createNumber("recruitId", Long.class);

    public final EnumPath<RecruitStatus> recruitStatus = createEnum("recruitStatus", RecruitStatus.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QTeamRecruitment(String variable) {
        this(TeamRecruitment.class, forVariable(variable), INITS);
    }

    public QTeamRecruitment(Path<? extends TeamRecruitment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamRecruitment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamRecruitment(PathMetadata metadata, PathInits inits) {
        this(TeamRecruitment.class, metadata, inits);
    }

    public QTeamRecruitment(Class<? extends TeamRecruitment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.externalActivity = inits.isInitialized("externalActivity") ? new QExternalActivity(forProperty("externalActivity")) : null;
    }

}

