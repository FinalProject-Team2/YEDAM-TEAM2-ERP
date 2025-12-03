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
}
