package CamNecT.CamNecT_Server.domain.certificate.repository;

import CamNecT.CamNecT_Server.domain.certificate.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findAllByUser_UserIdOrderByAcquiredDateDesc(Long userId);
}