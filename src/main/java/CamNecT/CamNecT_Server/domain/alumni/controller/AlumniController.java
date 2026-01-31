package CamNecT.CamNecT_Server.domain.alumni.controller;

import CamNecT.CamNecT_Server.domain.alumni.dto.AlumniPreviewResponse;
import CamNecT.CamNecT_Server.domain.alumni.service.AlumniService;
import CamNecT.CamNecT_Server.global.common.auth.UserId;
import CamNecT.CamNecT_Server.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Alumni")
public class AlumniController {

    private final AlumniService alumniService;

    @GetMapping
    public ApiResponse<List<AlumniPreviewResponse>> searchAlumni(
            @UserId Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "tags", required = false) List<Long> tagIdList
    ) {
        return ApiResponse.success(alumniService.searchAlumni(userId, name, tagIdList));
    }
}
