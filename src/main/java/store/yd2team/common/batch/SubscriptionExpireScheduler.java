package store.yd2team.common.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpireScheduler {

    private final SubscriptionExpireBatchService batchService;

    /**
     * 매일 새벽 1시 실행
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void runExpireBatch() {
        log.info("[SUBS BATCH] 구독 만료 배치 시작");
        batchService.expireSubscriptions();
        log.info("[SUBS BATCH] 구독 만료 배치 종료");
    }
}
