package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.mapper.WkTyMapper;
import store.yd2team.insa.service.DayVO;
import store.yd2team.insa.service.HldyVO;
import store.yd2team.insa.service.HldyWkBasiVO;
import store.yd2team.insa.service.WkTyService;

@Service
@RequiredArgsConstructor
@Transactional
public class WkTyServiceImpl implements WkTyService {

    private final WkTyMapper wkTyMapper;

    // ======================= 휴일 =======================

    @Override
    public List<HldyVO> selectLegalHlDyList() {
        return wkTyMapper.selectLegalHlDyList();
    }

    @Override
    public List<HldyVO> selectCompanyHlDyList(String vendId) {
        return wkTyMapper.selectCompanyHlDyList(vendId);
    }

    @Override
    public void insertHlDy(HldyVO vo) {
        wkTyMapper.insertHlDy(vo);
    }

    @Override
    public void updateHlDy(HldyVO vo) {
        wkTyMapper.updateHlDy(vo);
    }

    @Override
    public void deleteHlDy(Integer hldyNo) {
        wkTyMapper.deleteHlDy(hldyNo);
    }

    // =================== 휴일 근무시간 기준 ===================

    @Override
    public List<HldyWkBasiVO> getHldyWkBasiList(HldyWkBasiVO searchVO) {
        String vendId = LoginSession.getVendId();
        searchVO.setVendId(vendId);
        return wkTyMapper.selectHldyWkBasiList(searchVO);
    }

    @Override
    public HldyWkBasiVO getHldyWkBasi(Long basiNo) {
        String vendId = LoginSession.getVendId();
        return wkTyMapper.selectHldyWkBasiByNo(basiNo, vendId);
    }

    @Override
    public int insertHldyWkBasi(HldyWkBasiVO vo) {
        String empId  = LoginSession.getEmpId();
        String vendId = LoginSession.getVendId();
        vo.setCreaBy(empId);
        vo.setVendId(vendId);
        return wkTyMapper.insertHldyWkBasi(vo);
    }

    @Override
    public int updateHldyWkBasi(HldyWkBasiVO vo) {
        String empId  = LoginSession.getEmpId();
        String vendId = LoginSession.getVendId();
        vo.setUpdtBy(empId);
        vo.setVendId(vendId);
        return wkTyMapper.updateHldyWkBasi(vo);
    }

    @Override
    public int deleteHldyWkBasi(Long basiNo) {
        String vendId = LoginSession.getVendId();
        // 자식 요일 먼저 삭제
        wkTyMapper.deleteDayByBasiNo(basiNo, vendId);
        // 기준 삭제
        return wkTyMapper.deleteHldyWkBasi(basiNo, vendId);
    }

    /**
     * 기준 + 요일 한 번에 저장 (신규/수정 공통)
     * - hldy_ty / wk_de / hldy_de 없이, 요일제만 사용
     */
    @Override
    public Long saveHldyWkBasi(HldyWkBasiVO vo, List<String> dayList) {

        String empId  = LoginSession.getEmpId();
        String vendId = LoginSession.getVendId();

        vo.setVendId(vendId);
        Long basiNo = vo.getBasiNo();

        if (basiNo == null) {
            // INSERT
            vo.setCreaBy(empId);
            wkTyMapper.insertHldyWkBasi(vo);   // selectKey로 basiNo 세팅
            basiNo = vo.getBasiNo();
        } else {
            // UPDATE
            vo.setUpdtBy(empId);
            wkTyMapper.updateHldyWkBasi(vo);
            // 기존 요일 전체 삭제
            wkTyMapper.deleteDayByBasiNo(basiNo, vendId);
        }

        // 요일 재등록 (요일제만 사용)
        if (dayList != null) {
            for (String dayNm : dayList) {
                if (dayNm == null || dayNm.isBlank()) continue;

                DayVO d = new DayVO();
                d.setBasiNo(basiNo);
                d.setDayNm(dayNm);
                d.setCreaBy(empId);
                wkTyMapper.insertDay(d);
            }
        }

        return basiNo;
    }

    // ======================== 요일 ========================

    @Override
    public List<DayVO> getDayListByBasiNo(Long basiNo) {
        String vendId = LoginSession.getVendId();
        return wkTyMapper.selectDayListByBasiNo(basiNo, vendId);
    }

    @Override
    public int insertDay(DayVO vo) {
        String empId = LoginSession.getEmpId();
        vo.setCreaBy(empId);
        return wkTyMapper.insertDay(vo);
    }

    @Override
    public int deleteDay(Long dayNo) {
        String vendId = LoginSession.getVendId();
        return wkTyMapper.deleteDay(dayNo, vendId);
    }
}
