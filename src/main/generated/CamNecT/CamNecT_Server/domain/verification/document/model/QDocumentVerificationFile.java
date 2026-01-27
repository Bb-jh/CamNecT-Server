package CamNecT.CamNecT_Server.domain.verification.document.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDocumentVerificationFile is a Querydsl query type for DocumentVerificationFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDocumentVerificationFile extends EntityPathBase<DocumentVerificationFile> {

    private static final long serialVersionUID = -154524587L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDocumentVerificationFile documentVerificationFile = new QDocumentVerificationFile("documentVerificationFile");

    public final StringPath contentType = createString("contentType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originalFilename = createString("originalFilename");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public final StringPath storageKey = createString("storageKey");

    public final QDocumentVerificationSubmission submission;

    public final DateTimePath<java.time.LocalDateTime> uploadedAt = createDateTime("uploadedAt", java.time.LocalDateTime.class);

    public QDocumentVerificationFile(String variable) {
        this(DocumentVerificationFile.class, forVariable(variable), INITS);
    }

    public QDocumentVerificationFile(Path<? extends DocumentVerificationFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDocumentVerificationFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDocumentVerificationFile(PathMetadata metadata, PathInits inits) {
        this(DocumentVerificationFile.class, metadata, inits);
    }

    public QDocumentVerificationFile(Class<? extends DocumentVerificationFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.submission = inits.isInitialized("submission") ? new QDocumentVerificationSubmission(forProperty("submission")) : null;
    }

}

