package CamNecT.CamNecT_Server.global.tag.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QColleges is a Querydsl query type for Colleges
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColleges extends EntityPathBase<Colleges> {

    private static final long serialVersionUID = 1826875220L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QColleges colleges = new QColleges("colleges");

    public final StringPath collegeCode = createString("collegeCode");

    public final NumberPath<Long> collegeId = createNumber("collegeId", Long.class);

    public final StringPath collegeNameEng = createString("collegeNameEng");

    public final StringPath collegeNameKor = createString("collegeNameKor");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QInstitutions institution;

    public final BooleanPath isActive = createBoolean("isActive");

    public final NumberPath<Integer> sortOrder = createNumber("sortOrder", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QColleges(String variable) {
        this(Colleges.class, forVariable(variable), INITS);
    }

    public QColleges(Path<? extends Colleges> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QColleges(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QColleges(PathMetadata metadata, PathInits inits) {
        this(Colleges.class, metadata, inits);
    }

    public QColleges(Class<? extends Colleges> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new QInstitutions(forProperty("institution")) : null;
    }

}

