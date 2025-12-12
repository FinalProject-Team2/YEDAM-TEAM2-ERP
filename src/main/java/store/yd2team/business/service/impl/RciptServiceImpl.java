package store.yd2team.business.service.impl;

import java.util.List;

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
/*    
    // 조회 고객사 auto complete(고객코드, 고객사명)
    @Override
    public List<RciptVO> searchCustcomId(String keyword) {
        return rciptMapper.searchCustcomId(keyword);
    }
    @Override
    public List<RciptVO> searchCustcomName(String keyword) {
        return rciptMapper.searchCustcomName(keyword);
    }
*/
    
    //입금내역
    @Override
    @Transactional
    public int insertRciptDetail(RciptVO vo) {

        vo.setVendId(LoginSession.getVendId());
        vo.setCreaBy(LoginSession.getEmpId());
        vo.setUpdtBy(LoginSession.getEmpId());

        rciptMapper.callInsertRciptProcedure(vo);
        
        System.out.println(">>> RESULT_MSG FROM PROCEDURE = " + vo.getResultMsg());

        // OUT 파라미터에 담긴 결과 체크
        if (vo.getResultMsg() != null && vo.getResultMsg().startsWith("SUCCESS")) {
            return 1;
        } else {
            return 0;
        }
    }
}
