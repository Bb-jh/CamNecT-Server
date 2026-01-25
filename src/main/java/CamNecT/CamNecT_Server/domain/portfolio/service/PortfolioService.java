package CamNecT.CamNecT_Server.domain.portfolio.service;

import CamNecT.CamNecT_Server.domain.portfolio.dto.request.PortfolioRequest;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioDetailResponse;
import CamNecT.CamNecT_Server.domain.portfolio.dto.response.PortfolioPreviewResponse;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioAsset;
import CamNecT.CamNecT_Server.domain.portfolio.model.PortfolioProject;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioAssetRepository;
import CamNecT.CamNecT_Server.domain.portfolio.repository.PortfolioRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.errorcode.ErrorCode;
import CamNecT.CamNecT_Server.global.common.response.errorcode.bydomains.UserErrorCode;
import CamNecT.CamNecT_Server.global.common.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final S3Service s3Service;

    public List<PortfolioPreviewResponse> portfolioPreview(Long userId) {

        return portfolioRepository.findPreviewsByUserId(userId);

    }

    public PortfolioDetailResponse portfolioDetail(Long userId, Long portfolioId) {

        PortfolioProject portfolio = portfolioRepository.findById(portfolioId).orElseThrow(
                ()-> new CustomException(UserErrorCode.PORTFOLIO_NOT_FOUND));

        List<PortfolioAsset> portfolioAssets = portfolioAssetRepository.findAssetsByPortfolioId(portfolioId);

        Boolean isMine = userId.equals(portfolio.getUserId());

        return new PortfolioDetailResponse(isMine, portfolio, portfolioAssets);

    }

    @Transactional
    public PortfolioPreviewResponse create(Long userId, PortfolioRequest request) {

        String thumbUrl = s3Service.uploadFile(request.thumbnailUrl());

        //요청에 따라 PortfolioProject 생성
        PortfolioProject project = PortfolioProject.builder()
                .userId(userId)
                .title(request.projectTitle())
                .description(request.description())
                .thumbnailUrl(thumbUrl)
                .startDate(request.startedAt())
                .endDate(request.endedAt())// 예시 포맷
                .review(request.review())
                .isPublic(true) // 기본값 설정
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        //첨부파일 처리 및 연관관계 설정
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            for (int i = 0; i < request.attachments().size(); i++) {
                MultipartFile file = request.attachments().get(i);
                if (!file.isEmpty()) {
                    String fileUrl = s3Service.uploadFile(file);

                    // PortfolioAsset 생성 (이미지/문자열 등 타입 판단 로직 추가 가능)
                    PortfolioAsset asset = PortfolioAsset.builder()
                            .portfolioProject(project) // 양방향 연관관계 주입
                            .fileUrl(fileUrl)
                            .type(file.getContentType()) // MIME 타입 저장
                            .sortOrder(i + 1)
                            .createdAt(LocalDateTime.now())
                            .build();

                    project.getAssets().add(asset);
                }
            }
        }

        //저장 (CascadeType.ALL에 의해 assets도 같이 저장)
        PortfolioProject savedProject = portfolioRepository.save(project);

        return PortfolioPreviewResponse.of(savedProject);
    }

    @Transactional
    public PortfolioPreviewResponse update(Long userId, Long portfolioId, PortfolioRequest request) {
        PortfolioProject project = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(UserErrorCode.PORTFOLIO_NOT_FOUND));

        // 권한 체크
        if (!project.getUserId().equals(userId)) {
            throw new CustomException(UserErrorCode.PORTFOLIO_FORBIDDEN);
        }

        //썸네일 수정 (새 파일이 들어온 경우)
        if (request.thumbnailUrl() != null && !request.thumbnailUrl().isEmpty()) {
            // 기존 S3 파일 삭제
            s3Service.deleteFile(project.getThumbnailUrl());
            String newThumbUrl = s3Service.uploadFile(request.thumbnailUrl());

            project.updateThumbnail(newThumbUrl);
        }

        //텍스트 정보 업데이트
        project.updateInfo(request.projectTitle(), request.description(), request.review(), request.startedAt(), request.endedAt());

        //첨부파일(Assets) 수정
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            // 기존 S3 파일들 삭제
            project.getAssets().forEach(asset -> s3Service.deleteFile(asset.getFileUrl()));
            project.getAssets().clear();

            //새 파일 업로드 및 등록
            for (int i = 0; i < request.attachments().size(); i++) {
                String url = s3Service.uploadFile(request.attachments().get(i));
                project.getAssets().add(PortfolioAsset.builder()
                        .portfolioProject(project)
                        .fileUrl(url)
                        .sortOrder(i + 1)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        return PortfolioPreviewResponse.of(project);
    }

    public void delete(Long userId, Long portfolioId) {
        PortfolioProject project = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(UserErrorCode.PORTFOLIO_NOT_FOUND));

        if (!project.getUserId().equals(userId)) {
            throw new CustomException(UserErrorCode.PORTFOLIO_FORBIDDEN);
        }

        //S3에서 썸네일 삭제
        s3Service.deleteFile(project.getThumbnailUrl());

        //S3에서 모든 첨부파일 삭제
        project.getAssets().forEach(asset -> s3Service.deleteFile(asset.getFileUrl()));

        //DB 삭제
        portfolioRepository.delete(project);
    }

}
