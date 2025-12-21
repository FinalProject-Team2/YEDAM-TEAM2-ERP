package store.yd2team.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.RciptMapper;
import store.yd2team.business.service.RciptService;
import store.yd2team.business.service.RciptVO;
import store.yd2team.common.util.LoginSession;

@Service
@RequiredArgsConstructor
public class RciptServiceImpl implements RciptService {

	private final RciptMapper rciptMapper;
	
	// 조회
    @Override
    public List<RciptVO> searchRcipt(RciptVO searchVO) {
        return rciptMapper.selectRciptList(searchVO);
    }
    
    // 입금처리
    @Override
    @Transactional
    public void saveRcipt(RciptVO vo) {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        // 필수값 검증
        if (vo.getRciptId() == null || vo.getRciptId().isEmpty()) {
            throw new RuntimeException("입금 대상 채권 정보가 없습니다.");
        }

        if (vo.getRciptAmt() == null || vo.getRciptAmt() <= 0) {
            throw new RuntimeException("입금금액이 올바르지 않습니다.");
        }

        // custcomId는 검증/로그용
        if (vo.getCustcomId() == null || vo.getCustcomId().isEmpty()) {
            throw new RuntimeException("고객사 정보가 없습니다.");
        }

        // 프로시저 파라미터 구성
        Map<String, Object> param = new HashMap<>();
        param.put("vendId", vendId);
        param.put("rciptId", vo.getRciptId()); 
        param.put("custcomId", vo.getCustcomId());
        param.put("rciptDt", vo.getRciptDt());
        param.put("rciptAmt", vo.getRciptAmt());
        param.put("pmtMtd", vo.getPmtMtd());
        param.put("rm", vo.getRm());
        param.put("empId", empId);

        // 프로시저 호출
        rciptMapper.callRciptPayment(param);
    }
    
    
    // 입금 상세내역
    @Override
    @Transactional(readOnly = true)
    public List<RciptVO> selectRciptDetailList(String rciptId) {

        if (rciptId == null || rciptId.isEmpty()) {
            throw new RuntimeException("채권 ID가 없습니다.");
        }

        return rciptMapper.selectRciptDetailList(rciptId);
    }

}
