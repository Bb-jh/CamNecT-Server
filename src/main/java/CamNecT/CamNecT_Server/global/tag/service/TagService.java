package CamNecT.CamNecT_Server.global.tag.service;

import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.model.TagAttributeName;

import java.util.List;

public interface TagService {

    List<Tag> list(TagAttributeName attribute);

    List<Tag> list(TagAttributeName attribute, String category);

    List<Tag> search(String keyword, int limit);

    void seedDefaults(); // 서버 시작 시 기본값 주입용
}
