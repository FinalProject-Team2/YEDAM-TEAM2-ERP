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

    void callPrcCalcSalyItems(Map<String, Object> p);


    Long selectNextSpecItemNo();

    void insertSpecItems(Map<String, Object> p);

    /** ✅ (추가) spec+grp+itemId 기준으로 있으면 UPDATE, 없으면 INSERT */
    void mergeSpecItem(Map<String, Object> p);

    void updateSalySpecTotals(Map<String, Object> p);

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
    
 // grpNo → grpNm 변환
    String selectGrpNm(@Param("vendId") String vendId,
                       @Param("grpNo") Long grpNo);

    // spec + grpNm 기준 전체 삭제
    void deleteSpecItemsBySpecAndGrpNm(@Param("salySpecId") String salySpecId,
                                       @Param("grpNm") String grpNm);

    // spec + grpNm 기준, key(itemTy_dispNo) NOT IN 삭제
    void deleteSpecItemsBySpecAndDispNosNotIn(Map<String, Object> p);

    // 저장된 급여명세 항목 조회 (grpNm 기준)
    List<SalySpecItemVO> selectSalySpecItemsByGrpNm(@Param("salySpecId") String salySpecId,
                                                    @Param("grpNm") String grpNm);
 // ✅ 재조회용: saly_spec_id 기준 전체 조회
    List<SalySpecItemVO> selectSalySpecItemsBySpecId(@Param("salySpecId") String salySpecId);

}
