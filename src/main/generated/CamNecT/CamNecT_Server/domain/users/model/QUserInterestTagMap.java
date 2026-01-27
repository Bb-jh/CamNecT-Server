package CamNecT.CamNecT_Server.domain.users.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserInterestTagMap is a Querydsl query type for UserInterestTagMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserInterestTagMap extends EntityPathBase<UserInterestTagMap> {

    private static final long serialVersionUID = 1738116032L;

    public static final QUserInterestTagMap userInterestTagMap = new QUserInterestTagMap("userInterestTagMap");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> tagId = createNumber("tagId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserInterestTagMap(String variable) {
        super(UserInterestTagMap.class, forVariable(variable));
    }

    public QUserInterestTagMap(Path<? extends UserInterestTagMap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserInterestTagMap(PathMetadata metadata) {
        super(UserInterestTagMap.class, metadata);
    }

}

