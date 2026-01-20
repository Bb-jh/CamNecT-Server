package CamNecT.CamNecT_Server.domain.profile.service;

import CamNecT.CamNecT_Server.domain.certificate.dto.response.CertificateResponse;
import CamNecT.CamNecT_Server.domain.certificate.repository.CertificateRepository;
import CamNecT.CamNecT_Server.domain.education.dto.response.EducationResponse;
import CamNecT.CamNecT_Server.domain.education.repository.EducationRepository;
import CamNecT.CamNecT_Server.domain.experience.dto.response.ExperienceResponse;
import CamNecT.CamNecT_Server.domain.experience.repository.ExperienceRepository;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileResponse;
import CamNecT.CamNecT_Server.domain.users.model.UserProfile;
import CamNecT.CamNecT_Server.domain.users.model.Users;
import CamNecT.CamNecT_Server.domain.users.repository.UserFollowRepository;
import CamNecT.CamNecT_Server.domain.users.repository.UserProfileRepository;
import CamNecT.CamNecT_Server.domain.users.repository.UserRepository;
import CamNecT.CamNecT_Server.domain.users.repository.UserTagMapRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.ErrorCode;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final ExperienceRepository experienceRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserFollowRepository userFollowRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserTagMapRepository userTagMapRepository;
    private final EducationRepository educationRepository;


    public ProfileResponse getUserProfile(Long profileUserId) {

        Users user = userRepository.findByUserId(profileUserId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        UserProfile userProfile = userProfileRepository.findByUserId(profileUserId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        int following = userFollowRepository.countByFollowingId(profileUserId);
        int follower = userFollowRepository.countByFollowerId(profileUserId);

        List<PortfolioPreviewResponse> portfolioPreviewResponses = portfolioRepository.findPreviewsByUserId(profileUserId);

        List<EducationResponse> educationResponses = educationRepository.findAllByUserIdWithDetails(profileUserId)
                .stream()
                .map(EducationResponse::from)
                .toList();
        List<ExperienceResponse> experienceList = experienceRepository.findAllByUser_UserIdOrderByStartDateDesc(profileUserId).stream()
                .map(ExperienceResponse::from)
                .toList();
        List<CertificateResponse> certificateList = certificateRepository.findAllByUser_UserIdOrderByAcquiredDateDesc(profileUserId).stream()
                .map(CertificateResponse::from)
                .toList();

        List<Tag> tagList = userTagMapRepository.findAllTagsByUserId(profileUserId);

        //Boolean isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(userId, profileUserId);

        return new ProfileResponse(
                user.getName(),
                userProfile,
                following,
                follower,
                portfolioPreviewResponses,
                educationResponses,
                experienceList,
                certificateList,
                tagList
                //isFollowing
        );
    }
}
