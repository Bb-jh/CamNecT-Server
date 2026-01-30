package CamNecT.CamNecT_Server.domain.activity.model.external_activity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QExternalActivityAttachment is a Querydsl query type for ExternalActivityAttachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QExternalActivityAttachment extends EntityPathBase<ExternalActivityAttachment> {

    private static final long serialVersionUID = -1577251442L;

    public static final QExternalActivityAttachment externalActivityAttachment = new QExternalActivityAttachment("externalActivityAttachment");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> externalActivity = createNumber("externalActivity", Long.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QExternalActivityAttachment(String variable) {
        super(ExternalActivityAttachment.class, forVariable(variable));
    }

    public QExternalActivityAttachment(Path<? extends ExternalActivityAttachment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QExternalActivityAttachment(PathMetadata metadata) {
        super(ExternalActivityAttachment.class, metadata);
    }

}

