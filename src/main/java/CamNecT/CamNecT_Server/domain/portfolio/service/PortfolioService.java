package CamNecT.CamNecT_Server.domain.portfolio.service;

import CamNecT.CamNecT_Server.domain.portfolio.dto.request.PortfolioRequest;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioAssetView;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioDetailResponse;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioAsset;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioAssetRepository;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.UserErrorCode;
import CamNecT.CamNecT_Server.global.storage.model.UploadPurpose;
import CamNecT.CamNecT_Server.global.storage.model.UploadRefType;
import CamNecT.CamNecT_Server.global.storage.model.UploadTicket;
import CamNecT.CamNecT_Server.global.storage.repository.UploadTicketRepository;
import CamNecT.CamNecT_Server.global.storage.service.FileStorage;
import CamNecT.CamNecT_Server.global.storage.service.PresignEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;

    private final PresignEngine presignEngine;
    private final UploadTicketRepository ticketRepo;
    private final FileStorage fileStorage;

    public List<PortfolioPreviewResponse> portfolioPreview(Long userId) {
        List<PortfolioPreviewResponse> rows = portfolioRepository.findPreviewsByUserId(userId);

        return rows.stream()
                .map(r -> {
                    String key = r.thumbnailUrl();
                    if (!StringUtils.hasText(key) || "기본이미지".equals(key)) {
                        return new PortfolioPreviewResponse(r.portfolioId(), r.title(), null);
                    }
                    try {
                        String url = presignEngine.presignDownload(key, "thumbnail", null).downloadUrl();
                        return new PortfolioPreviewResponse(r.portfolioId(), r.title(), url);
                    } catch (Exception e) {
                        return new PortfolioPreviewResponse(r.portfolioId(), r.title(), null);
                    }
                })
                .toList();
    }

    public PortfolioDetailResponse portfolioDetail(Long userId, Long portfolioId) {

        PortfolioProject portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(UserErrorCode.PORTFOLIO_NOT_FOUND));

        boolean isMine = Objects.equals(userId, portfolio.getUserId());

        String thumbKey = portfolio.getThumbnailUrl();
        String thumbUrl = presignOrNull(thumbKey, "thumbnail", null);
        portfolio.setThumbnailUrl(thumbUrl);

        List<PortfolioAsset> assets = portfolioAssetRepository.findAssetsByPortfolioId(portfolioId);

        List<PortfolioAssetView> views = assets.stream()
                .map(a -> new PortfolioAssetView(
                        a.getAssetId(),
                        a.getType(),
                        a.getFileUrl(),
                        presignOrNull(a.getFileUrl(), "asset", a.getType()),
                        a.getSortOrder(),
                        a.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new PortfolioDetailResponse(isMine, portfolio, views);
    }

    @Transactional
    public PortfolioPreviewResponse create(Long userId, PortfolioRequest request) {

        //요청에 따라 PortfolioProject 생성
        PortfolioProject project = PortfolioProject.builder()
                .userId(userId)
                .title(request.projectTitle())
                .description(request.description())
                .thumbnailUrl("기본이미지")
                .startDate(request.startedAt())
                .endDate(request.endedAt())
                .review(request.review())
                .isPublic(true)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        PortfolioProject saved = portfolioRepository.save(project);

        // thumbnail
        if (StringUtils.hasText(request.thumbnailKey())) {
            presignEngine.consume(
                    userId,
                    UploadPurpose.PORTFOLIO_ATTACHMENT,
                    UploadRefType.PORTFOLIO,
                    saved.getPortfolioId(),
                    request.thumbnailKey()
            );
            saved.updateThumbnail(request.thumbnailKey());
        }

        // attachments
        List<String> keys = (request.attachmentKeys() == null) ? List.of() : request.attachmentKeys();
        int order = 1;

        for (String key : keys) {
            if (!StringUtils.hasText(key)) continue;

            presignEngine.consume(
                    userId,
                    UploadPurpose.PORTFOLIO_ATTACHMENT,
                    UploadRefType.PORTFOLIO,
                    saved.getPortfolioId(),
                    key
            );

            String type = ticketRepo.findByStorageKey(key)
                    .map(UploadTicket::getContentType)
                    .orElse("");

            saved.getAssets().add(PortfolioAsset.builder()
                    .portfolioProject(saved)
                    .type(type)
                    .fileUrl(key)
                    .sortOrder(order++)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        return new PortfolioPreviewResponse(
                saved.getPortfolioId(),
                saved.getTitle(),
                presignOrNull(saved.getThumbnailUrl(), "thumbnail", null)
        );
    }

    @Transactional
    public PortfolioPreviewResponse update(Long userId, Long portfolioId, PortfolioRequest request) {
        PortfolioProject project = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(UserErrorCode.PORTFOLIO_NOT_FOUND));

        // 권한 체크
        if (!project.getUserId().equals(userId)) {
            throw new CustomException(UserErrorCode.PORTFOLIO_FORBIDDEN);
        }

        Set<String> deleteAfterCommit = new HashSet<>();

        // thumbnail 교체
        if (StringUtils.hasText(request.thumbnailKey())
                && !Objects.equals(request.thumbnailKey(), project.getThumbnailUrl())) {

            if (StringUtils.hasText(project.getThumbnailUrl()) && !"기본이미지".equals(project.getThumbnailUrl())) {
                deleteAfterCommit.add(project.getThumbnailUrl());
            }

            presignEngine.consume(
                    userId,
                    UploadPurpose.PORTFOLIO_ATTACHMENT,
                    UploadRefType.PORTFOLIO,
                    project.getPortfolioId(),
                    request.thumbnailKey()
            );

            project.updateThumbnail(request.thumbnailKey());
        }

        project.updateInfo(
                request.projectTitle(),
                request.description(),
                request.review(),
                request.startedAt(),
                request.endedAt()
        );

        // attachments 교체(요청이 들어온 경우만)
        if (request.attachmentKeys() != null) {
            // 기존 것 삭제 후보로 쌓기
            project.getAssets().forEach(a -> {
                if (StringUtils.hasText(a.getFileUrl())) deleteAfterCommit.add(a.getFileUrl());
            });
            project.getAssets().clear();

            int order = 1;
            for (String key : request.attachmentKeys()) {
                if (!StringUtils.hasText(key)) continue;

                presignEngine.consume(
                        userId,
                        UploadPurpose.PORTFOLIO_ATTACHMENT,
                        UploadRefType.PORTFOLIO,
                        project.getPortfolioId(),
                        key
                );

                String type = ticketRepo.findByStorageKey(key)
                        .map(UploadTicket::getContentType)
                        .orElse("");

                project.getAssets().add(PortfolioAsset.builder()
                        .portfolioProject(project)
                        .type(type)
                        .fileUrl(key)
                        .sortOrder(order++)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }
        registerAfterCommitDelete(deleteAfterCommit);

        return new PortfolioPreviewResponse(
                project.getPortfolioId(),
                project.getTitle(),
                presignOrNull(project.getThumbnailUrl(), "thumbnail", null)
        );
    }

    public void delete(Long userId, Long portfolioId) {
        PortfolioProject project = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(UserErrorCode.PORTFOLIO_NOT_FOUND));

        if (!project.getUserId().equals(userId)) {
            throw new CustomException(UserErrorCode.PORTFOLIO_FORBIDDEN);
        }

        Set<String> deleteAfterCommit = new HashSet<>();

        if (StringUtils.hasText(project.getThumbnailUrl()) && !"기본이미지".equals(project.getThumbnailUrl())) {
            deleteAfterCommit.add(project.getThumbnailUrl());
        }

        project.getAssets().forEach(a -> {
            if (StringUtils.hasText(a.getFileUrl())) deleteAfterCommit.add(a.getFileUrl());
        });

        portfolioRepository.delete(project);

        registerAfterCommitDelete(deleteAfterCommit);
    }

    private String presignOrNull(String key, String filename, String contentType) {
        if (!StringUtils.hasText(key) || "기본이미지".equals(key)) return null;
        try {
            return presignEngine.presignDownload(key, filename, contentType).downloadUrl();
        } catch (Exception e) {
            return null;
        }
    }

    private void registerAfterCommitDelete(Set<String> keys) {
        if (keys == null || keys.isEmpty()) return;

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (String key : keys) {
                        try { fileStorage.delete(key); } catch (Exception ignored) {}
                    }
                }
            });
        } else {
            for (String key : keys) {
                try { fileStorage.delete(key); } catch (Exception ignored) {}
            }
        }
    }

}
