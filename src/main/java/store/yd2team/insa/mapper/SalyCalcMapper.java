package store.yd2team.insa.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.AllowDucVO;
import store.yd2team.insa.service.CalGrpVO;
import store.yd2team.insa.service.SalySpecCalcViewVO;
import store.yd2team.insa.service.SalySpecItemVO;

@Mapper
public interface SalyCalcMapper {

    List<SalySpecCalcViewVO> selectSalySpecCalcList(@Param("salyLedgId") String salyLedgId,
                                                    @Param("vendId") String vendId);

    String selectEmpIdBySpecId(@Param("salySpecId") String salySpecId);

    /**
     * ✅ OUT CURSOR 결과는 Map의 o_result로 꺼내는 방식
     */
    void callPrcCalcSalyItems(Map<String, Object> p);

    /** ✅ FIX: grpNo 제거 (spec 기준 전체 삭제) */
    void deleteSpecItemsBySpec(@Param("salySpecId") String salySpecId);

    void insertSpecItems(Map<String, Object> p);

    void updateSalySpecTotals(Map<String, Object> p);

    List<SalySpecItemVO> selectSalySpecItems(@Param("salySpecId") String salySpecId,
                                             @Param("grpNo") Long grpNo);

    List<CalGrpVO> selectCalcGroupList(@Param("vendId") String vendId);

    List<AllowDucVO> selectAllowListForCalc(@Param("vendId") String vendId,
                                            @Param("grpNo") Long grpNo);

    List<AllowDucVO> selectDucListForCalc(@Param("vendId") String vendId,
                                          @Param("grpNo") Long grpNo);

    void deleteGrpItemsByGrpNo(@Param("vendId") String vendId,
                              @Param("grpNo") Long grpNo);

    void insertGrpItems(Map<String, Object> p);

    Long selectNextCalcGrpNo();

    void insertCalcGroup(@Param("grpNo") Long grpNo,
                         @Param("vendId") String vendId,
                         @Param("grpNm") String grpNm,
                         @Param("creaBy") String creaBy);

    void updateCalcGroup(@Param("vendId") String vendId,
                         @Param("grpNo") Long grpNo,
                         @Param("grpNm") String grpNm,
                         @Param("updtBy") String updtBy);

    void deleteCalcGroup(@Param("vendId") String vendId,
                         @Param("grpNo") Long grpNo);
}
