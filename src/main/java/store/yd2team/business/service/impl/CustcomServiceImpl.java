package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.CustcomMapper;
import store.yd2team.business.service.CustcomService;
import store.yd2team.business.service.CustcomVO;

@Service
@RequiredArgsConstructor
public class CustcomServiceImpl implements CustcomService {

    private final CustcomMapper custcomMapper;

    // 조회
    @Override
    public List<CustcomVO> searchCustcom(CustcomVO vo) {
        return custcomMapper.searchCustcom(vo);
    }
    
    // 공통코드 조회
    @Override
    public List<CustcomVO> getBSType() {
    	return custcomMapper.selectBSType();
    }
    
    
    // 저장
    @Override
    public int saveNewCust(CustcomVO vo) throws Exception {
        System.out.println("### Service saveNewCust 호출 ###");
        int result = custcomMapper.insertCustcom(vo);
        System.out.println("### result = " + result);
        return 1;   // 변화 건수 상관없이 성공 처리
    }

}
