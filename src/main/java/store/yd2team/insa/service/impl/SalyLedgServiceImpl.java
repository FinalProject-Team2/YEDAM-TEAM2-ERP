// src/main/java/store/yd2team/insa/service/impl/SalyLedgServiceImpl.java
package store.yd2team.insa.service.impl;

import java.util.ArrayList;
import java.util.List;

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
            // 급여대장 ID 채번
            String newLedgId = salyLedgMapper.selectNewSalyLedgId();
            vo.setSalyLedgId(newLedgId);
            salyLedgMapper.insertSalyLedg(vo);
        } else {
            // 벤더 체크 (안 맞으면 예외)
            SalyLedgVO origin = salyLedgMapper.selectSalyLedgById(vo.getSalyLedgId());
            if (origin == null) {
                throw new IllegalArgumentException("존재하지 않는 급여대장입니다.");
            }
            if (origin.getVendId() != null && !origin.getVendId().equals(vendId)) {
                throw new IllegalArgumentException("삭제/수정 권한이 없습니다.");
            }

            // 급여대장 수정
            salyLedgMapper.updateSalyLedg(vo);
            // 기존 명세서 삭제 후 다시 생성
            salyLedgMapper.deleteSalySpecByLedgId(vo.getSalyLedgId());
        }

        String salyLedgId = vo.getSalyLedgId();

        // 명세서 리스트 생성
        List<SalySpecVO> specList = new ArrayList<>();
        for (String empId : vo.getEmpIdList()) {
            if (empId == null || empId.isEmpty()) continue;

            // 사원 1명당 명세서 ID 1개씩 채번
            String newSpecId = salyLedgMapper.selectNewSalySpecId();

            SalySpecVO spec = SalySpecVO.builder()
                    .salySpecId(newSpecId)
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
                                            String salyLedgNm,
                                            String payDtStart,
                                            String payDtEnd) {

        String nm   = (salyLedgNm != null && !salyLedgNm.isBlank()) ? salyLedgNm : null;
        String dtS  = (payDtStart != null && !payDtStart.isBlank()) ? payDtStart : null;
        String dtE  = (payDtEnd != null && !payDtEnd.isBlank()) ? payDtEnd : null;

        return salyLedgMapper.selectSalyLedgList(vendId, nm, dtS, dtE);
    }

    @Override
    public SalyLedgVO getSalyLedgDetail(String salyLedgId, String vendId) {
        SalyLedgVO vo = salyLedgMapper.selectSalyLedgById(salyLedgId);
        if (vo == null) return null;

        if (vo.getVendId() != null && !vo.getVendId().equals(vendId)) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }

        List<String> empIds = salyLedgMapper.selectEmpIdListByLedgId(salyLedgId);
        vo.setEmpIdList(empIds);

        return vo;
    }

    @Override
    @Transactional
    public void deleteSalyLedg(String salyLedgId, String vendId) {

        SalyLedgVO vo = salyLedgMapper.selectSalyLedgById(salyLedgId);
        if (vo == null) {
            throw new IllegalArgumentException("존재하지 않는 급여대장입니다.");
        }

        if (vo.getVendId() != null && !vo.getVendId().equals(vendId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

     // ✅ 1) 명세서항목 먼저 삭제 (FK 때문에 필수)
        salyLedgMapper.deleteSalySpecItemByLedgId(salyLedgId);

        // ✅ 2) 명세서 삭제
        salyLedgMapper.deleteSalySpecByLedgId(salyLedgId);

        // ✅ 3) 급여대장 삭제
        salyLedgMapper.deleteSalyLedg(salyLedgId);
    }
    @Override
    @Transactional
    public void confirmSalyLedg(String salyLedgId, String vendId, String loginEmpId) {

        int uncalcCnt = salyLedgMapper.countUncalculatedSpec(salyLedgId, vendId);
        if (uncalcCnt > 0) {
            throw new IllegalStateException(
                "급여계산이 완료되지 않은 명세서가 있어 확정할 수 없습니다. (미계산 " + uncalcCnt + "건)"
            );
        }

        salyLedgMapper.updateSalyLedgStatus(
            salyLedgId,
            vendId,
            "sal2",   // 확정
            loginEmpId
        );
    }

    @Override
    @Transactional
    public void cancelConfirmSalyLedg(String salyLedgId, String vendId, String loginEmpId) {

        // sal3(지급완료)는 프론트에서 이미 차단 중
        salyLedgMapper.updateSalyLedgStatus(
            salyLedgId,
            vendId,
            "sal1",   // 미확정
            loginEmpId
        );
    }

}
