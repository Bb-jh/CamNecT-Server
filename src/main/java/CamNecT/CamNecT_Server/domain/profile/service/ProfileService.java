package CamNecT.CamNecT_Server.domain.profile.service;

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
import CamNecT.CamNecT_Server.global.tag.model.Certificate;
import CamNecT.CamNecT_Server.global.tag.model.Experience;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.CertificateRepository;
import CamNecT.CamNecT_Server.global.tag.repository.ExperienceRepository;
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


    public ProfileResponse getUserProfile(Long profileUserId) {

        Users user = userRepository.findByUserId(profileUserId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND));
        UserProfile userProfile = userProfileRepository.findByUserId(profileUserId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND));

        int following = userFollowRepository.countByFollowingId(profileUserId);
        int follower = userFollowRepository.countByFollowerId(profileUserId);

        List<PortfolioPreviewResponse> portfolioPreviewResponses = portfolioRepository.findPreviewsByUserId(profileUserId);

        List<Experience> experienceList = experienceRepository.findAllByUserId(profileUserId);
        List<Certificate> certificateList = certificateRepository.findAllByUserId(profileUserId);

        List<Tag> tagList = userTagMapRepository.findAllTagsByUserId(profileUserId);

        //Boolean isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(userId, profileUserId);

        return new ProfileResponse(
                user.getName(),
                userProfile,
                following,
                follower,
                portfolioPreviewResponses,
                experienceList,
                certificateList,
                tagList
                //isFollowing
        );
    }
}
