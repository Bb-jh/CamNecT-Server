package CamNecT.CamNecT_Server.global.storage.repository;

import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadTicketRepository extends JpaRepository<UploadTicket, Long> {
    Optional<UploadTicket> findByStorageKey(String storageKey);

    long countByUserIdAndPurposeAndStatus(Long userId, UploadPurpose purpose, UploadTicket.Status status);
}