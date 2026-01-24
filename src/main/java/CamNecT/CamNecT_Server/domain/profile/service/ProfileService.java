package CamNecT.CamNecT_Server.domain.profile.service;

import CamNecT.CamNecT_Server.domain.certificate.dto.response.CertificateResponse;
import CamNecT.CamNecT_Server.domain.certificate.repository.CertificateRepository;
import CamNecT.CamNecT_Server.domain.education.dto.response.EducationResponse;
import CamNecT.CamNecT_Server.domain.education.repository.EducationRepository;
import CamNecT.CamNecT_Server.domain.experience.dto.response.ExperienceResponse;
import CamNecT.CamNecT_Server.domain.experience.repository.ExperienceRepository;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateProfileTagsRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateProfileBasicsRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileStatusResponse;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileResponse;
import CamNecT.CamNecT_Server.domain.users.model.*;
import CamNecT.CamNecT_Server.domain.users.repository.*;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.ErrorCode;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
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

    // =========================================================
    // 프로필이미지, 메모 등 저장
    // =========================================================
    @Transactional
    public ProfileStatusResponse updateBasicsSettings(Long userId, UpdateProfileBasicsRequest req) {

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        requireOnboardingPending(user);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        userProfile.updateOnboardingProfile(req.bio(), req.profileImageUrl());

        return new ProfileStatusResponse(user.getStatus());
    }

    // =========================================================
    // 분야별 태그(관심분야 태그) 선택
    // =========================================================
    @Transactional
    public ProfileStatusResponse updateProfileTags(Long userId, UpdateProfileTagsRequest req) {

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        requireOnboardingPending(user);

        List<Long> tagIds = (req.tagIds() == null) ? List.of() : req.tagIds().stream().distinct().toList();

        // 존재 검증 (스킵 허용이면 empty OK)
        if (!tagIds.isEmpty()) {
            var tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new IllegalArgumentException("존재하지 않는 태그가 포함되어 있습니다.");
            }
        }

        // 프로필 노출 태그 저장(user_tag_map)
        userTagMapRepository.deleteAllByUserId(userId);
        userTagMapRepository.saveAll(
                tagIds.stream()
                        .map(tid -> UserTagMap.builder().userId(userId).tagId(tid).build())
                        .toList()
        );
        return new ProfileStatusResponse(user.getStatus());
    }

    private void requireOnboardingPending(Users user) {
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalStateException("정지된 계정입니다.");
        }
        if (user.getStatus() == UserStatus.EMAIL_PENDING) {
            throw new IllegalStateException("이메일 인증이 필요합니다.");
        }
    }


}
