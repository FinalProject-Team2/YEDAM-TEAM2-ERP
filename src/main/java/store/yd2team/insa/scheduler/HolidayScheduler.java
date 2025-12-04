package store.yd2team.insa.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.WkTyMapper;
import store.yd2team.insa.service.LawHolidayService;

@Component
@RequiredArgsConstructor
public class HolidayScheduler {

    private final LawHolidayService lawHolidayService;
    private final WkTyMapper wkTyMapper;

    /**
     * 매년 12월 31일 밤 11시 59분에:
     *  1) 기존 법정 공휴일(공통, vend_id IS NULL)의 사용여부를 e2로 변경
     *  2) 다음 해 법정 공휴일을 e1 상태로 새로 등록
     */
    @Scheduled(cron = "0 59 23 31 12 *")
    public void refreshLegalHolidaysAtYearEnd() {

        int thisYear = LocalDate.now().getYear();
        int nextYear = thisYear + 1;

        System.out.println("[SCHEDULER] 기존 법정 공휴일(e1 → e2) 처리 시작");
        int updated = wkTyMapper.deactivateAllLegalHlDy();
        System.out.println("[SCHEDULER] 기존 법정 공휴일 사용여부 변경 완료: " + updated + "건 (e1 → e2)");

        System.out.println("[SCHEDULER] " + nextYear + "년 법정 공휴일 등록 시작");
        lawHolidayService.fetchAndSaveHolidays(nextYear);
        System.out.println("[SCHEDULER] " + nextYear + "년 법정 공휴일 등록 완료");
    }
}
