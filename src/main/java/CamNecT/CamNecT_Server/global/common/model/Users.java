package CamNecT.CamNecT_Server.global.common.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users") // 테이블명
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone_num", length = 20, unique = true)
    private String phoneNum;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @CreationTimestamp // 생성 시 자동으로 시간 입력
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // 수정 시 자동으로 시간 업데이트
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}