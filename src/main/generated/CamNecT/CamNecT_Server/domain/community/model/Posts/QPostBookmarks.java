package CamNecT.CamNecT_Server.domain.community.model.Posts;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostBookmarks is a Querydsl query type for PostBookmarks
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostBookmarks extends EntityPathBase<PostBookmarks> {

    private static final long serialVersionUID = -975960806L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostBookmarks postBookmarks = new QPostBookmarks("postBookmarks");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPosts post;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostBookmarks(String variable) {
        this(PostBookmarks.class, forVariable(variable), INITS);
    }

    public QPostBookmarks(Path<? extends PostBookmarks> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostBookmarks(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostBookmarks(PathMetadata metadata, PathInits inits) {
        this(PostBookmarks.class, metadata, inits);
    }

    public QPostBookmarks(Class<? extends PostBookmarks> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPosts(forProperty("post"), inits.get("post")) : null;
    }

}

