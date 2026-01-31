package CamNecT.CamNecT_Server.domain.profile.service;

import CamNecT.CamNecT_Server.domain.certificate.dto.response.CertificateResponse;
import CamNecT.CamNecT_Server.domain.certificate.repository.CertificateRepository;
import CamNecT.CamNecT_Server.domain.education.dto.response.EducationResponse;
import CamNecT.CamNecT_Server.domain.education.repository.EducationRepository;
import CamNecT.CamNecT_Server.domain.experience.dto.response.ExperienceResponse;
import CamNecT.CamNecT_Server.domain.experience.repository.ExperienceRepository;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateOnboardingRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.request.UpdateProfileTagsRequest;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileStatusResponse;
import CamNecT.CamNecT_Server.domain.profile.dto.response.ProfileResponse;
import CamNecT.CamNecT_Server.domain.users.model.*;
import CamNecT.CamNecT_Server.domain.users.repository.*;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.ErrorCode;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.AuthErrorCode;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.StorageErrorCode;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.UserErrorCode;
import CamNecT.CamNecT_Server.global.storage.dto.request.PresignUploadRequest;
import CamNecT.CamNecT_Server.global.storage.dto.response.PresignUploadResponse;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadRefType;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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
    private final PresignEngine presignEngine;

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

        List<ProfileResponse.TagDto> tags = userTagMapRepository.findAllTagsByUserId(profileUserId).stream()
                .map(t -> new ProfileResponse.TagDto(t.getId(), t.getName(), t.getCategory(), t.getAttribute().getName()))
                .toList();

        ProfileResponse.ProfileBasicsDto basicProfile = new ProfileResponse.ProfileBasicsDto(
                userProfile.getBio(),
                userProfile.getOpenToCoffeeChat(),
                userProfile.getProfileImageUrl(),
                userProfile.getStudentNo(),
                userProfile.getYearLevel(),
                userProfile.getInstitutionId(),
                userProfile.getMajorId()
        );

        //Boolean isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(userId, profileUserId);

        return new ProfileResponse(
                user.getUserId(),
                user.getName(),
                basicProfile,
                following,
                follower,
                portfolioPreviewResponses,
                educationResponses,
                experienceList,
                certificateList,
                tags
                //isFollowing
        );
    }

    @Transactional
    public ProfileStatusResponse updateOnboarding(Long userId, UpdateOnboardingRequest req) {

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        requireEmailVerifiedAndNotSuspended(user);

        // 1) 프로필 기본(bio, image)
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_PROFILE_NOT_FOUND));

        // 2) 프로필 이미지 key 처리 (presign temp -> final 승격)
        String finalProfileImageKey = null;
        if (req.profileImageKey() != null && !req.profileImageKey().isBlank()) {

            String finalPrefix = "profile/user-" + userId + "/images"; // final 위치(원하는 대로)
            finalProfileImageKey = presignEngine.consume(
                    userId,
                    UploadPurpose.PROFILE_IMAGE,
                    UploadRefType.USER_PROFILE,
                    userId, // refId는 userId로 두면 깔끔
                    req.profileImageKey(), // tempKey
                    finalPrefix
            );
        }

        userProfile.updateOnboardingProfile(req.bio(), finalProfileImageKey);

        // 3) 태그 replace
        List<Long> tagIds = (req.tagIds() == null) ? List.of() : req.tagIds().stream().distinct().toList();

        if (!tagIds.isEmpty()) {
            var tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new CustomException(UserErrorCode.INVALID_TAG_IDS);
            }
        }

        userTagMapRepository.deleteAllByUserId(userId);

        if (!tagIds.isEmpty()) {
            userTagMapRepository.saveAll(
                    tagIds.stream()
                            .map(tid -> UserTagMap.builder().userId(userId).tagId(tid).build())
                            .toList()
            );
        }

        return new ProfileStatusResponse(user.getStatus());
    }


    // =========================================================
    // 분야별 태그(관심분야 태그) 선택
    // =========================================================
    @Transactional
    public ProfileStatusResponse updateProfileTags(Long userId, UpdateProfileTagsRequest req) {

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        requireEmailVerifiedAndNotSuspended(user);

        List<Long> tagIds = (req.tagIds() == null) ? List.of() : req.tagIds().stream().distinct().toList();

        // 존재 검증 (스킵 허용이면 empty OK)
        if (!tagIds.isEmpty()) {
            var tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new CustomException(UserErrorCode.INVALID_TAG_IDS);
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

    public PresignUploadResponse presignProfileImageUpload(Long userId, PresignUploadRequest req) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        requireEmailVerifiedAndNotSuspended(user);

        String ct = normalize(req.contentType());
        if (req.size() == null || req.size() <= 0) {
            throw new CustomException(StorageErrorCode.STORAGE_EMPTY_FILE);
        }

        String keyPrefix = "profile/user-" + userId + "/images";
        return presignEngine.issueUpload(
                userId,
                UploadPurpose.PROFILE_IMAGE,
                keyPrefix,
                ct,
                req.size(),
                req.originalFilename()
        );
    }

    private void requireEmailVerifiedAndNotSuspended(Users user) {
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new CustomException(UserErrorCode.USER_SUSPENDED);
        }
        if (user.getStatus() == UserStatus.EMAIL_PENDING) {
            throw new CustomException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    private String normalize(String ct) {
        return (ct == null) ? "" : ct.trim().toLowerCase(Locale.ROOT);
    }


}
