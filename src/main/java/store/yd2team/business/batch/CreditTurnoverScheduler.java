package store.yd2team.business.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.yd2team.business.service.CreditService;

@Component
@RequiredArgsConstructor
public class CreditTurnoverScheduler {

    private final CreditService creditService;
    //                 초 분 시 일 월 요일
    @Scheduled(cron = "0 10 2 * * *", zone = "Asia/Seoul")
    public void runDaily() {
        creditService.runTurnoverBatch();
    }
}