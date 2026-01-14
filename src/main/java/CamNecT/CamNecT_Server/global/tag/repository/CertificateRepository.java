package CamNecT.CamNecT_Server.global.tag.repository;

import CamNecT.CamNecT_Server.global.tag.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    //user id로 모든 자격증 조회
    List<Certificate> findAllByUserId(Long userId);

}
