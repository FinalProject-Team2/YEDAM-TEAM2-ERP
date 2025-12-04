package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.DayVO;
import store.yd2team.insa.service.HldyVO;
import store.yd2team.insa.service.HldyWkBasiVO;

@Mapper
public interface WkTyMapper {

    // =================== 휴일 ===================
    List<HldyVO> selectLegalHlDyList();

    List<HldyVO> selectCompanyHlDyList(@Param("vendId") String vendId);

    void insertHlDy(HldyVO vo);

    void updateHlDy(HldyVO vo);

    void deleteHlDy(@Param("hldyNo") Integer hldyNo);

    // ===== 휴일 근무시간 기준 =====
    List<HldyWkBasiVO> selectHldyWkBasiList(HldyWkBasiVO searchVO);

    HldyWkBasiVO selectHldyWkBasiByNo(@Param("basiNo") Long basiNo,
                                      @Param("vendId") String vendId);

    int insertHldyWkBasi(HldyWkBasiVO vo);

    int updateHldyWkBasi(HldyWkBasiVO vo);

    int deleteHldyWkBasi(@Param("basiNo") Long basiNo,
                         @Param("vendId") String vendId);

    // ===== 요일 =====
    List<DayVO> selectDayListByBasiNo(@Param("basiNo") Long basiNo,
                                      @Param("vendId") String vendId);

    int insertDay(DayVO vo);

    int deleteDay(@Param("dayNo") Long dayNo,
                  @Param("vendId") String vendId);

    int deleteDayByBasiNo(@Param("basiNo") Long basiNo,
                          @Param("vendId") String vendId);
    
    // ===== 법정 공휴일 자동등록용 =====

    /**
     * 법정 공휴일(공통, vend_id IS NULL) 중
     * 같은 날짜(YYYYMMDD)가 이미 등록돼 있는지 체크
     */
    int existsLegalHlDyByLocdate(@Param("locdate") String locdate);

    /**
     * 공공데이터에서 받은 법정 공휴일을 tb_hldy_basi 에 INSERT
     * - 법정 공휴일은 vend_id를 NULL로 넣어서 "공통"으로 관리
     */
    int insertLegalHlDyFromApi(@Param("hldyNm") String hldyNm,
                               @Param("locdate") String locdate);

    /**
     * 기존 법정 공휴일(공통, vend_id IS NULL) 중
     * 사용중(e1)인 것들을 전부 미사용(e2)로 변경
     */
    int deactivateAllLegalHlDy();
}
