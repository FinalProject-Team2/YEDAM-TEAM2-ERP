// src/main/java/store/yd2team/insa/service/impl/SalyLedgServiceImpl.java
package store.yd2team.insa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.insa.mapper.SalyLedgMapper;
import store.yd2team.insa.service.EmpVO;
import store.yd2team.insa.service.SalyLedgService;
import store.yd2team.insa.service.SalyLedgVO;
import store.yd2team.insa.service.SalySpecVO;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalyLedgServiceImpl implements SalyLedgService {

    private final SalyLedgMapper salyLedgMapper;

    @Override
    public List<EmpVO> getEmpListForSaly(String vendId, String deptId, String empNm) {
        return salyLedgMapper.selectEmpListForSaly(vendId, deptId, empNm);
    }

    private String newId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    @Transactional
    public String saveSalyLedg(SalyLedgVO vo, String vendId, String loginEmpId) {

        if (vo.getEmpIdList() == null || vo.getEmpIdList().isEmpty()) {
            throw new IllegalArgumentException("선택된 사원이 없습니다.");
        }

        vo.setVendId(vendId);
        vo.setCreaBy(loginEmpId);
        vo.setUpdtBy(loginEmpId);
        vo.setRcnt(vo.getEmpIdList().size());

        if (vo.getTtPayAmt() == null) {
            vo.setTtPayAmt(0d);
        }
        // 상태 코드가 비어있으면 미확정(sal1)
        if (vo.getSalyLedgSt() == null || vo.getSalyLedgSt().isEmpty()) {
            vo.setSalyLedgSt("sal1");
        }

        boolean isNew = (vo.getSalyLedgId() == null || vo.getSalyLedgId().isEmpty());

        if (isNew) {
            vo.setSalyLedgId(newId("SL_"));
            salyLedgMapper.insertSalyLedg(vo);
        } else {
            salyLedgMapper.updateSalyLedg(vo);
            salyLedgMapper.deleteSalySpecByLedgId(vo.getSalyLedgId());
        }

        String salyLedgId = vo.getSalyLedgId();

        List<SalySpecVO> specList = new ArrayList<>();
        for (String empId : vo.getEmpIdList()) {
            if (empId == null || empId.isEmpty()) continue;

            SalySpecVO spec = SalySpecVO.builder()
                    .salySpecId(newId("SP_"))
                    .salyLedgId(salyLedgId)
                    .empId(empId)
                    .payAmt(0L)
                    .ttDucAmt(0L)
                    .actPayAmt(0L)
                    .creaBy(loginEmpId)
                    .updtBy(loginEmpId)
                    .build();
            specList.add(spec);
        }

        if (!specList.isEmpty()) {
            salyLedgMapper.insertSalySpecList(specList);
        }

        return salyLedgId;
    }

    @Override
    public List<SalyLedgVO> getSalyLedgList(String vendId,
                                            String deptId,
                                            String salyLedgNm,
                                            String payDtStart,
                                            String payDtEnd) {

        // 공백 문자열은 null로 넘겨서 <if test="... != null and ... != ''"> 에 걸리도록
        String dept = (deptId != null && !deptId.isBlank()) ? deptId : null;
        String nm   = (salyLedgNm != null && !salyLedgNm.isBlank()) ? salyLedgNm : null;
        String dtS  = (payDtStart != null && !payDtStart.isBlank()) ? payDtStart : null;
        String dtE  = (payDtEnd != null && !payDtEnd.isBlank()) ? payDtEnd : null;

        return salyLedgMapper.selectSalyLedgList(vendId, dept, nm, dtS, dtE);
    }
}
