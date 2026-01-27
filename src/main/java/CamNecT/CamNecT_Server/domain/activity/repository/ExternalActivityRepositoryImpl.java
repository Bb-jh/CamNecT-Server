package CamNecT.CamNecT_Server.domain.activity.repository;

import CamNecT.CamNecT_Server.domain.activity.dto.response.ActivityPreviewResponse;
import CamNecT.CamNecT_Server.domain.activity.model.*;
import CamNecT.CamNecT_Server.domain.users.model.QUserTagMap;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ExternalActivityRepositoryImpl implements ExternalActivityRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ActivityPreviewResponse> findAllByFilters(
            Long userId, ActivityCategory category, List<Long> tagIds,
            String title, String sortType, Pageable pageable) {

        QExternalActivity activity = QExternalActivity.externalActivity;
        QExternalActivityTag activityTag = QExternalActivityTag.externalActivityTag;
        QTeamRecruitment recruitment = QTeamRecruitment.teamRecruitment;
        QUserTagMap userTag = QUserTagMap.userTagMap;

        //기본 필터링 (카테고리, 제목)
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(activity.category.eq(category));
        if (title != null) builder.and(activity.title.contains(title));

        //태그 교집합 필터링 (모든 태그를 포함하는 경우)
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                builder.and(activity.activityId.in(
                        JPAExpressions.select(activityTag.externalActivity.activityId)
                                .from(activityTag)
                                .where(activityTag.tag.id.eq(tagId))
                ));
            }
        }

        // 3. 정렬 조건 생성
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortType, activity, recruitment, userId);

        List<ExternalActivity> results = queryFactory
                .selectFrom(activity)
                .leftJoin(recruitment).on(recruitment.externalActivity.eq(activity))
                .where(builder)
                .orderBy(orderSpecifier, activity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4. DTO 변환 (태그 리스트 포함)
        List<ActivityPreviewResponse> content = results.stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(content, pageable, results.size());
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortType, QExternalActivity activity, QTeamRecruitment recruitment, Long userId) {
        return switch (sortType) {
            case "LATEST" -> activity.applyStartDate.desc();
            case "CLOSING" -> activity.applyEndDate.asc();
            case "RECRUIT_COUNT" -> recruitment.count().desc(); // 팀원 모집 많은 순
            case "RECOMMEND" -> {
                // 유저 태그와 매칭되는 개수 순 정렬 (간략화된 예시)
                yield activity.activityId.count().desc();
            }
            default -> activity.createdAt.desc();
        };
    }

    private ActivityPreviewResponse toResponse(ExternalActivity activity) {
        return new ActivityPreviewResponse(
                activity.getActivityId(),
                activity.getTitle(),
                activity.getContext(),
                null, // ThumbnailUrl 필드가 엔티티에 없으므로 임시 null 처리
                null  // Tags 리스트 임시 null 처리
        );
    }
}