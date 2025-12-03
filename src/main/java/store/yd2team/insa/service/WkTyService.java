package store.yd2team.insa.service;

import java.util.List;

public interface WkTyService {

    // ===== 휴일 =====
    List<HldyVO> selectLegalHlDyList();

    List<HldyVO> selectCompanyHlDyList(String vendId);

    void insertHlDy(HldyVO vo);

    void updateHlDy(HldyVO vo);

    void deleteHlDy(Integer hldyNo);

    // ===== 휴일 근무시간 기준 =====
    List<HldyWkBasiVO> getHldyWkBasiList(HldyWkBasiVO searchVO);

    HldyWkBasiVO getHldyWkBasi(Long basiNo);

    int insertHldyWkBasi(HldyWkBasiVO vo);

    int updateHldyWkBasi(HldyWkBasiVO vo);

    int deleteHldyWkBasi(Long basiNo);

    /** 기준 + 요일 한 번에 저장 (신규/수정 공통) */
    Long saveHldyWkBasi(HldyWkBasiVO vo, List<String> dayList);

    // ===== 요일 =====
    List<DayVO> getDayListByBasiNo(Long basiNo);

    int insertDay(DayVO vo);

    int deleteDay(Long dayNo);
}
