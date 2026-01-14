package CamNecT.CamNecT_Server.global.tag.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag_attribute")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TagAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_attribute_id")
    private Long id;

    // ERD: ENUM('DEPARTMENT','TOPIC','CUSTOM')
    @Enumerated(EnumType.STRING)
    @Column(
            name = "name",
            nullable = false,
            length = 20,
            columnDefinition = "ENUM('DEPARTMENT','TOPIC','CUSTOM')"
    )
    private TagAttributeName name;
}
