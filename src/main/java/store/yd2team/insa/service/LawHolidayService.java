package store.yd2team.insa.service;

/**
 * 법정 공휴일을 공공데이터에서 가져와
 * tb_hldy_basi 테이블(법정 공휴일 영역)에 저장하는 서비스
 */
public interface LawHolidayService {

    /**
     * 특정 연도의 법정 공휴일을 공공데이터 API에서 가져와 저장.
     *
     * @param year 대상 연도 (예: 2025)
     */
    void fetchAndSaveHolidays(int year);
}
