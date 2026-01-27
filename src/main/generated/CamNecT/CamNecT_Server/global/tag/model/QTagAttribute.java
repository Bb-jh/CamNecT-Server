package CamNecT.CamNecT_Server.global.tag.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTagAttribute is a Querydsl query type for TagAttribute
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTagAttribute extends EntityPathBase<TagAttribute> {

    private static final long serialVersionUID = -1941470790L;

    public static final QTagAttribute tagAttribute = new QTagAttribute("tagAttribute");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<TagAttributeName> name = createEnum("name", TagAttributeName.class);

    public QTagAttribute(String variable) {
        super(TagAttribute.class, forVariable(variable));
    }

    public QTagAttribute(Path<? extends TagAttribute> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTagAttribute(PathMetadata metadata) {
        super(TagAttribute.class, metadata);
    }

}

