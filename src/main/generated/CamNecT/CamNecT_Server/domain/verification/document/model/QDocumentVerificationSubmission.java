package CamNecT.CamNecT_Server.domain.verification.document.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDocumentVerificationSubmission is a Querydsl query type for DocumentVerificationSubmission
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDocumentVerificationSubmission extends EntityPathBase<DocumentVerificationSubmission> {

    private static final long serialVersionUID = -719445147L;

    public static final QDocumentVerificationSubmission documentVerificationSubmission = new QDocumentVerificationSubmission("documentVerificationSubmission");

    public final EnumPath<DocumentType> docType = createEnum("docType", DocumentType.class);

    public final ListPath<DocumentVerificationFile, QDocumentVerificationFile> files = this.<DocumentVerificationFile, QDocumentVerificationFile>createList("files", DocumentVerificationFile.class, QDocumentVerificationFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath rejectReason = createString("rejectReason");

    public final DateTimePath<java.time.LocalDateTime> reviewedAt = createDateTime("reviewedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> reviewerAdminId = createNumber("reviewerAdminId", Long.class);

    public final EnumPath<VerificationStatus> status = createEnum("status", VerificationStatus.class);

    public final DateTimePath<java.time.LocalDateTime> submittedAt = createDateTime("submittedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QDocumentVerificationSubmission(String variable) {
        super(DocumentVerificationSubmission.class, forVariable(variable));
    }

    public QDocumentVerificationSubmission(Path<? extends DocumentVerificationSubmission> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDocumentVerificationSubmission(PathMetadata metadata) {
        super(DocumentVerificationSubmission.class, metadata);
    }

}

