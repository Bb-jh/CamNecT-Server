package CamNecT.CamNecT_Server.domain.activity.service;

import CamNecT.CamNecT_Server.domain.activity.dto.request.ActivityRequest;
import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityDetailResponse;
import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.enums.ActivityCategory;
import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivity;
import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivityAttachment;
import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivityBookmark;
import CamNecT.CamNecT_Server.domain.activity.model.external_activity.ExternalActivityTag;
import CamNecT.CamNecT_Server.domain.activity.model.recruitment.TeamRecruitment;
import CamNecT.CamNecT_Server.domain.activity.repository.external_activity.ExternalActivityAttachmentRepository;
import CamNecT.CamNecT_Server.domain.activity.repository.external_activity.ExternalActivityBookmarkRepository;
import CamNecT.CamNecT_Server.domain.activity.repository.external_activity.ExternalActivityRepository;
import CamNecT.CamNecT_Server.domain.activity.repository.external_activity.ExternalActivityTagRepository;
import CamNecT.CamNecT_Server.domain.activity.repository.recruitment.TeamRecruitmentRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.ActivityErrorCode;
import CamNecT.CamNecT_Server.global.tag.model.Tag;
import CamNecT.CamNecT_Server.global.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActivityService {

    private final ExternalActivityRepository activityRepository;
    private final ExternalActivityTagRepository activityTagRepository;
    private final ExternalActivityAttachmentRepository activityAttachmentRepository;
    private final ExternalActivityBookmarkRepository activityBookmarkRepository;
    private final TagRepository tagRepository;
    private final TeamRecruitmentRepository teamRecruitmentRepository;

    public Slice<ActivityPreviewResponse> getActivities(
            Long userId,
            ActivityCategory category,
            List<Long> tagIds,
            String title,
            String sortType,
            Pageable pageable) {

        return activityRepository.findActivitiesByCondition(
                userId, category, tagIds, title, sortType, pageable
        );
    }

    @Transactional
    public ExternalActivity create(Long userId, ActivityRequest request) {

        ExternalActivity activity = ExternalActivity.builder()
                .userId(userId)
                .title(request.title())
                .category(request.category())
                .context(request.content())
                .thumbnailUrl(request.thumbnailUrl())
                .build();

        ExternalActivity savedActivity = activityRepository.save(activity);

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            List<ExternalActivityTag> tags = request.tagIds().stream()
                    .map(tagId -> ExternalActivityTag.builder()
                            .activityId(savedActivity.getActivityId())
                            .tagId(tagId)
                            .build())
                    .collect(Collectors.toList());
            activityTagRepository.saveAll(tags);
        }

        if (request.attachmentUrl() != null && !request.attachmentUrl().isEmpty()) {
            List<ExternalActivityAttachment> attachments = request.attachmentUrl().stream()
                    .map(url -> ExternalActivityAttachment.builder()
                            .externalActivity(savedActivity.getActivityId())
                            .fileUrl(url)
                            .build())
                    .collect(Collectors.toList());
            activityAttachmentRepository.saveAll(attachments);

        }

        return savedActivity;
    }

    @Transactional(readOnly = true)
    public ActivityDetailResponse getActivityDetail(Long userId, Long activityId) {
        //메인 활동 조회
        ExternalActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new CustomException(ActivityErrorCode.ACTIVITY_NOT_FOUND));

        //대외활동, 취업 공고일 때만 첨부파일 리스트 조회
        List<ExternalActivityAttachment> attachments = null;

        if(activity.getCategory() == ActivityCategory.EXTERNAL || activity.getCategory() == ActivityCategory.RECRUITMENT)
            attachments = activityAttachmentRepository.findAllByExternalActivity(activityId);

        //태그 리스트 조회
        List<Long> tagIds = activityTagRepository.findAllByActivityId(activityId).stream()
                .map(ExternalActivityTag::getTagId)
                .toList();
        List<Tag> tagList = tagRepository.findAllById(tagIds);

        //팀원 공고 리스트 조회
        List<TeamRecruitment> recruitmentList = teamRecruitmentRepository.findAllByActivityId(activityId);

        // 본인 글 여부 확인
        boolean isMine = activity.getUserId() == null || activity.getUserId().equals(userId);

        return new ActivityDetailResponse(
                isMine,
                activity,
                attachments,
                tagList,
                recruitmentList
        );
    }

    @Transactional
    public void update(Long userId, Long activityId, ActivityRequest request) {

        ExternalActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new CustomException(ActivityErrorCode.ACTIVITY_NOT_FOUND));

        if (activity.getUserId() == null || !activity.getUserId().equals(userId))
            throw new CustomException(ActivityErrorCode.NOT_AUTHOR);

        activity.update(request);

        activityTagRepository.deleteByActivityId(activityId);
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            List<ExternalActivityTag> newTags = request.tagIds().stream()
                    .map(tagId -> ExternalActivityTag.builder()
                            .activityId(activityId)
                            .tagId(tagId)
                            .build())
                    .collect(Collectors.toList());
            activityTagRepository.saveAll(newTags);
        }

        activityAttachmentRepository.deleteByExternalActivity(activityId);
        if (request.attachmentUrl() != null && !request.attachmentUrl().isEmpty()) {
            List<ExternalActivityAttachment> newAttachments = request.attachmentUrl().stream()
                    .map(url -> ExternalActivityAttachment.builder()
                            .externalActivity(activityId)
                            .fileUrl(url)
                            .build())
                    .collect(Collectors.toList());
            activityAttachmentRepository.saveAll(newAttachments);
        }
    }

    @Transactional
    public void delete(Long activityId, Long userId) {
        ExternalActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new CustomException(ActivityErrorCode.ACTIVITY_NOT_FOUND));


        if (activity.getUserId() == null || !activity.getUserId().equals(userId))
            throw new CustomException(ActivityErrorCode.NOT_AUTHOR);

        activityTagRepository.deleteByActivityId(activityId);

        //todo : S3 저장 파일 삭제
        activityAttachmentRepository.deleteByExternalActivity(activityId);

        activityRepository.delete(activity);
    }

    @Transactional
    public boolean toggleActivityBookmark(Long userId, Long activityId) {
        //대외활동 존재 여부 확인
         if (!activityRepository.existsById(activityId)) {
             throw new CustomException(ActivityErrorCode.ACTIVITY_NOT_FOUND);
         }

        //북마크 존재 여부 확인
        Optional<ExternalActivityBookmark> bookmarkOpt = activityBookmarkRepository.findByUserIdAndActivityId(userId, activityId);

        if (bookmarkOpt.isPresent()) {
            //이미 존재하면 삭제
            activityBookmarkRepository.delete(bookmarkOpt.get());
            return false; // 해제됨을 반환
        } else {
            //존재하지 않으면 생성
            ExternalActivityBookmark newBookmark = ExternalActivityBookmark.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .build();
            activityBookmarkRepository.save(newBookmark);
            return true; // 등록됨을 반환
        }
    }
}