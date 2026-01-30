package CamNecT.CamNecT_Server.domain.activity.model.external_activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExternalActivityTag is a Querydsl query type for ExternalActivityTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExternalActivityTag extends EntityPathBase<ExternalActivityTag> {

    private static final long serialVersionUID = 2065784207L;

    public static final QExternalActivityTag externalActivityTag = new QExternalActivityTag("externalActivityTag");

    public final NumberPath<Long> activityId = createNumber("activityId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> tagId = createNumber("tagId", Long.class);

    public QExternalActivityTag(String variable) {
        super(ExternalActivityTag.class, forVariable(variable));
    }

    public QExternalActivityTag(Path<? extends ExternalActivityTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExternalActivityTag(PathMetadata metadata) {
        super(ExternalActivityTag.class, metadata);
    }

}

