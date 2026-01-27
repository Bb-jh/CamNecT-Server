package CamNecT.CamNecT_Server.domain.community.model.Posts;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPosts is a Querydsl query type for Posts
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPosts extends EntityPathBase<Posts> {

    private static final long serialVersionUID = 102133936L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosts posts = new QPosts("posts");

    public final EnumPath<CamNecT.CamNecT_Server.domain.community.model.enums.PostAccessType> accessType = createEnum("accessType", CamNecT.CamNecT_Server.domain.community.model.enums.PostAccessType.class);

    public final CamNecT.CamNecT_Server.domain.community.model.QBoards board;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAnonymous = createBoolean("isAnonymous");

    public final NumberPath<Integer> requiredPoints = createNumber("requiredPoints", Integer.class);

    public final EnumPath<CamNecT.CamNecT_Server.domain.community.model.enums.PostStatus> status = createEnum("status", CamNecT.CamNecT_Server.domain.community.model.enums.PostStatus.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPosts(String variable) {
        this(Posts.class, forVariable(variable), INITS);
    }

    public QPosts(Path<? extends Posts> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPosts(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPosts(PathMetadata metadata, PathInits inits) {
        this(Posts.class, metadata, inits);
    }

    public QPosts(Class<? extends Posts> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new CamNecT.CamNecT_Server.domain.community.model.QBoards(forProperty("board")) : null;
    }

}

