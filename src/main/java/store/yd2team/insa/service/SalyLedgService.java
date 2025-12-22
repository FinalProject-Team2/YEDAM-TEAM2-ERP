// src/main/java/store/yd2team/insa/service/SalyLedgService.java
package store.yd2team.insa.service;

import java.util.List;

public interface SalyLedgService {

    List<EmpVO> getEmpListForSaly(String vendId, String deptId, String empNm);

    String saveSalyLedg(SalyLedgVO vo, String vendId, String loginEmpId);

    List<SalyLedgVO> getSalyLedgList(
            String vendId,
            String salyLedgNm,
            String payDtStart,
            String payDtEnd
    );

    /** 급여대장 + 사원 ID 리스트(명세서 기준) */
    SalyLedgVO getSalyLedgDetail(String salyLedgId, String vendId);

    /** 급여대장 + 해당 명세서 전체 삭제 */
    void deleteSalyLedg(String salyLedgId, String vendId);
    void confirmSalyLedg(String salyLedgId, String vendId, String loginEmpId);
    void cancelConfirmSalyLedg(String salyLedgId, String vendId, String loginEmpId);

}
