package CamNecT.CamNecT_Server.domain.community.model.Posts;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostAccess is a Querydsl query type for PostAccess
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostAccess extends EntityPathBase<PostAccess> {

    private static final long serialVersionUID = 1393807207L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostAccess postAccess = new QPostAccess("postAccess");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> paidPoints = createNumber("paidPoints", Integer.class);

    public final QPosts post;

    public final DateTimePath<java.time.LocalDateTime> purchasedAt = createDateTime("purchasedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostAccess(String variable) {
        this(PostAccess.class, forVariable(variable), INITS);
    }

    public QPostAccess(Path<? extends PostAccess> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostAccess(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostAccess(PathMetadata metadata, PathInits inits) {
        this(PostAccess.class, metadata, inits);
    }

    public QPostAccess(Class<? extends PostAccess> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPosts(forProperty("post"), inits.get("post")) : null;
    }

}

