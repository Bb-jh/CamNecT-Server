package CamNecT.CamNecT_Server.domain.community.service;

import CamNecT.CamNecT_Server.domain.community.model.Boards;
import CamNecT.CamNecT_Server.domain.community.model.enums.BoardCode;
import CamNecT.CamNecT_Server.domain.community.repository.BoardsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BoardsInitializer implements ApplicationRunner {

    private final BoardsRepository boardsRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensure(BoardCode.INFO, "정보");
        ensure(BoardCode.QUESTION, "질문");
    }

    private void ensure(BoardCode code, String name) {
        if (boardsRepository.existsByCode(code)) return;

        try {
            boardsRepository.save(Boards.of(code, name));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 여러 인스턴스가 동시에 올라와서 누가 먼저 insert 했을 수 있음 → 무시
        }
    }
}
