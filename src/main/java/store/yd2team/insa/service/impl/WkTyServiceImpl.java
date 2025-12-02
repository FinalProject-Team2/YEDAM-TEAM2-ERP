package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.mapper.WkTyMapper;
import store.yd2team.insa.service.HldyVO;
import store.yd2team.insa.service.WkTyService;

@Service
@RequiredArgsConstructor
public class WkTyServiceImpl implements WkTyService {

    private final WkTyMapper wkTyMapper;

    /** 휴일 기준 전체 조회 */
    @Override
    public List<HldyVO> getHlDyList() {
        return wkTyMapper.selectHlDyList();
    }

    /** 휴일 단건 등록 */
    @Override
    @Transactional
    public int insertHlDy(HldyVO vo) {

        // 🔹 세션에서 공통값 가져오기
        String empId  = LoginSession.getEmpId();   // 작성자
        String vendId = LoginSession.getVendId();  // 회사코드

        // 🔹 NOT NULL 컬럼 강제 세팅
        vo.setCreaBy(empId);    // CREA_BY
        vo.setVendId(vendId);   // VEND_ID

        // 🔹 사용여부 코드 기본값 (널/공백이면 e1 = 사용)
        if (vo.getYnCode() == null || vo.getYnCode().isBlank()) {
            vo.setYnCode("e1");
        }

        return wkTyMapper.insertHlDy(vo);
    }

    /** 휴일 단건 수정 */
    @Override
    @Transactional
    public int updateHlDy(HldyVO vo) {

        String empId = LoginSession.getEmpId();  // 수정자
        vo.setUpdtBy(empId);

        if (vo.getYnCode() == null || vo.getYnCode().isBlank()) {
            vo.setYnCode("e1");
        }

        return wkTyMapper.updateHlDy(vo);
    }

    /** 휴일 단건 삭제 */
    @Override
    @Transactional
    public int deleteHlDy(Long hldyNo) {
        return wkTyMapper.deleteHlDy(hldyNo);
    }
}
