package CamNecT.CamNecT_Server.domain.community.repository;

import CamNecT.CamNecT_Server.domain.community.model.Boards;
import CamNecT.CamNecT_Server.domain.community.model.enums.BoardCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardsRepository extends JpaRepository<Boards, Long> {
    Optional<Boards> findByCode(BoardCode code);
    boolean existsByCode(BoardCode code);
}