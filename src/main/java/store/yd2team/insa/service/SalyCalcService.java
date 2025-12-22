package store.yd2team.insa.service;

import java.util.List;
import java.util.Map;

public interface SalyCalcService {

    List<SalySpecCalcViewVO> getSalySpecCalcList(String salyLedgId, String vendId);

    List<CalGrpVO> getCalcGroupList(String vendId);

    // grpNo null이면 전체, 있으면 그 그룹에 매핑된 항목만
    List<AllowDucVO> getAllowDucList(String vendId, Long grpNo);

    /**
     * ✅ 미리보기(확인 버튼)
     * - DB에 tb_saly_spec_item을 INSERT 하지 않는다.
     * - 서버에서 계산한 결과(항목/합계)를 반환만 한다.
     */
    Map<String, Object> previewSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                                        String vendId, String loginEmpId);

    /**
     * ✅ 저장(급여계산 모달의 저장 버튼)
     * - 미리보기에서 넘어온 계산 결과를 DB에 반영한다.
     */
    void savePreviewResult(String salyLedgId, Long grpNo, List<Map<String, Object>> previewList,
            String saveMode,
            String vendId, String loginEmpId);

    // (기존) 급여계산 실행 (선택 사원만) - 필요 시 유지
    void calculateSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                           String vendId, String loginEmpId);

    List<SalySpecItemVO> getSalySpecItems(String salySpecId, Long grpNo, String vendId);
 // grpNm 기준 급여명세 항목 조회 (재로딩용)
    List<SalySpecItemVO> getSalySpecItemsByGrpNm(String salySpecId,
                                                 String grpNm,
                                                 String vendId);


    // 단건 저장(필요시 유지)
    Long saveCalcGroup(String vendId, String empId, Long grpNo, String grpNm, List<String> itemIds);

    // wkTy 방식 saveAll
    void saveCalcGroupAll(String vendId, String empId,
                          List<Map<String, Object>> createdRows,
                          List<Map<String, Object>> updatedRows,
                          List<Map<String, Object>> deletedRows);

    void deleteCalcGroup(String vendId, Long grpNo);

    int deleteSpecItemsByLedgId(String salyLedgId, String vendId);

    int resetSalySpecTotalsByLedgId(String salyLedgId, String vendId, String updtBy);

    void resetSalyCalc(String salyLedgId, String vendId, String loginEmpId);


    Map<String, Object> getSavedCalcItems(String salyLedgId, Long grpNo, String vendId);
    
    List<SalySpecItemVO> getSalySpecItemsBySpecId(String salySpecId,
            String vendId);
    
}
