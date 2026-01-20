package CamNecT.CamNecT_Server.domain.point.service;

import CamNecT.CamNecT_Server.domain.point.model.PointSource;
import CamNecT.CamNecT_Server.domain.point.model.PointTransaction;
import CamNecT.CamNecT_Server.domain.point.model.PointWallet;
import CamNecT.CamNecT_Server.domain.point.model.TransactionType;
import CamNecT.CamNecT_Server.domain.point.repository.PointTransactionRepository;
import CamNecT.CamNecT_Server.domain.point.repository.PointWalletRepository;
import CamNecT.CamNecT_Server.global.common.exception.CustomException;
import CamNecT.CamNecT_Server.global.common.response.ErrorCode;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointWalletRepository walletRepository;
    private final PointTransactionRepository transactionRepository;

    //채택댓글 포인트 획득
    @Transactional
    public void earnPointByCommentSelection(Long userId, Long postId, int amount) {
        changePoint(
                userId,
                amount,
                TransactionType.EARN,
                PointSource.COMMENT_SELECTION,
                postId,
                null
        );
    }

    //커피챗 수락 포인트 획득
    @Transactional
    public void earnPointByCoffeeChatAcceptance(Long userId, Long requestId, int amount) {
        changePoint(
                userId,
                amount,
                TransactionType.EARN,
                PointSource.COFFEECHAT_ACCEPTANCE,
                null,
                requestId
        );
    }

    //포인트 사용
    @Transactional
    public void spendPoint(Long userId, int amount, PointSource pointSource) {
        changePoint(
                userId,
                amount,
                TransactionType.SPEND,
                pointSource,
                null,
                null
        );
    }

    @Transactional
    public void changePoint(
            Long userId,
            int amount,
            TransactionType transactionType,
            PointSource sourceType,
            Long postId,
            Long requestId
    ) {
        try {
            PointWallet wallet = getOrCreateWallet(userId);

            if (transactionType == TransactionType.SPEND && wallet.getBalance() < amount) {
                throw new CustomException(ErrorCode.INSUFFICIENT_POINT);
            }

            int signedAmount = transactionType == TransactionType.SPEND ? -amount : amount;
            wallet.updateBalance(signedAmount);

            PointTransaction transaction = PointTransaction.builder()
                    .userId(userId)
                    .postId(postId)
                    .requestId(requestId)
                    .pointChange(signedAmount)
                    .transactionType(transactionType)
                    .sourceType(sourceType)
                    .balanceAfter(wallet.getBalance())
                    .build();

            transactionRepository.save(transaction);

            walletRepository.flush();

        } catch (OptimisticLockException e) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
    }

    private PointWallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    try {
                        return walletRepository.save(
                                PointWallet.builder()
                                        .userId(userId)
                                        .balance(0)
                                        .build()
                        );
                    } catch (DataIntegrityViolationException e) {
                        return walletRepository.findByUserId(userId)
                                .orElseThrow();
                    }
                });
    }
}