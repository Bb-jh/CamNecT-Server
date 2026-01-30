package CamNecT.CamNecT_Server.domain.activity.model.external_activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExternalActivityBookmark is a Querydsl query type for ExternalActivityBookmark
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExternalActivityBookmark extends EntityPathBase<ExternalActivityBookmark> {

    private static final long serialVersionUID = -1565165471L;

    public static final QExternalActivityBookmark externalActivityBookmark = new QExternalActivityBookmark("externalActivityBookmark");

    public final NumberPath<Long> activityId = createNumber("activityId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QExternalActivityBookmark(String variable) {
        super(ExternalActivityBookmark.class, forVariable(variable));
    }

    public QExternalActivityBookmark(Path<? extends ExternalActivityBookmark> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExternalActivityBookmark(PathMetadata metadata) {
        super(ExternalActivityBookmark.class, metadata);
    }

}

