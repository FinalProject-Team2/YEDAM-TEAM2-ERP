package store.yd2team.insa.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.AllowDucVO;
import store.yd2team.insa.service.CalGrpVO;
import store.yd2team.insa.service.PayItemRowVO;
import store.yd2team.insa.service.SalySpecCalcViewVO;
import store.yd2team.insa.service.SalySpecItemVO;

@Mapper
public interface SalyCalcMapper {

    // 1) 급여계산 모달: 명세서(사원) 목록
    List<SalySpecCalcViewVO> selectSalySpecCalcList(
            @Param("salyLedgId") String salyLedgId,
            @Param("vendId") String vendId
    );

    // (계산용) salySpecId -> empId
    String selectEmpIdBySpecId(@Param("salySpecId") String salySpecId);

    // 2) 프로시저 호출
    List<PayItemRowVO> callPrcCalcSalyItems(Map<String, Object> param);

    // 3) 기존 항목 삭제
    int deleteSpecItemsBySpecAndGrp(@Param("salySpecId") String salySpecId, @Param("grpNo") Long grpNo);

    // 4) 항목 배치 insert
    int insertSpecItems(Map<String, Object> param);

    // 5) 합계 update
    int updateSalySpecTotals(Map<String, Object> param);

    // 6) 항목 조회
    List<SalySpecItemVO> selectSalySpecItems(
            @Param("salySpecId") String salySpecId,
            @Param("grpNo") Long grpNo
    );

    // 그룹 목록
    List<CalGrpVO> selectCalcGroupList(@Param("vendId") String vendId);

    // 그룹 항목 조회(매핑테이블 기반)
    List<AllowDucVO> selectAllowListForCalc(@Param("vendId") String vendId, @Param("grpNo") Long grpNo);
    List<AllowDucVO> selectDucListForCalc(@Param("vendId") String vendId, @Param("grpNo") Long grpNo);

    // 그룹 저장(매핑 갱신)
    int deleteGrpItemsByGrpNo(@Param("vendId") String vendId, @Param("grpNo") Long grpNo);
    int insertGrpItems(Map<String, Object> param);

    // 그룹 CRUD
    Long selectNextCalcGrpNo();
    int insertCalcGroup(@Param("grpNo") Long grpNo, @Param("vendId") String vendId, @Param("grpNm") String grpNm, @Param("creaBy") String creaBy);
    int updateCalcGroup(@Param("vendId") String vendId, @Param("grpNo") Long grpNo, @Param("grpNm") String grpNm, @Param("updtBy") String updtBy);
    int deleteCalcGroup(@Param("vendId") String vendId, @Param("grpNo") Long grpNo);
}
