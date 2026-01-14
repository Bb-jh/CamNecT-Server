package CamNecT.CamNecT_Server.global.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagSeedRunner implements ApplicationRunner {

    private final TagService tagService;

    @Override
    public void run(ApplicationArguments args) {
        tagService.seedDefaults();
    }
}
