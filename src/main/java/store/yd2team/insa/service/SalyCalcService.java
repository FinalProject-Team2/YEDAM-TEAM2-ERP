package store.yd2team.insa.service;

import java.util.List;
import java.util.Map;

public interface SalyCalcService {

    List<SalySpecCalcViewVO> getSalySpecCalcList(String salyLedgId, String vendId);

    List<CalGrpVO> getCalcGroupList(String vendId);

    // grpNo null이면 전체, 있으면 그 그룹에 매핑된 항목만
    List<AllowDucVO> getAllowDucList(String vendId, Long grpNo);

    // 급여계산 실행 (선택 사원만)
    void calculateSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                           String vendId, String loginEmpId);

    List<SalySpecItemVO> getSalySpecItems(String salySpecId, Long grpNo, String vendId);

    // 단건 저장(필요시 유지)
    Long saveCalcGroup(String vendId, String empId, Long grpNo, String grpNm, List<String> itemIds);

    // wkTy 방식 saveAll
    void saveCalcGroupAll(String vendId, String empId,
                          List<Map<String, Object>> createdRows,
                          List<Map<String, Object>> updatedRows,
                          List<Map<String, Object>> deletedRows);

    void deleteCalcGroup(String vendId, Long grpNo);
}
