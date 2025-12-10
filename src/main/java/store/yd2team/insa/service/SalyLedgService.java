// src/main/java/store/yd2team/insa/service/SalyLedgService.java
package store.yd2team.insa.service;

import java.util.List;

public interface SalyLedgService {

    List<EmpVO> getEmpListForSaly(String vendId, String deptId, String empNm);

    String saveSalyLedg(SalyLedgVO vo, String vendId, String loginEmpId);

    List<SalyLedgVO> getSalyLedgList(
            String vendId,
            String deptId,
            String salyLedgNm,
            String payDtStart,
            String payDtEnd
    );
}
