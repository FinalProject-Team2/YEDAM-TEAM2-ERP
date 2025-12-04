package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.CreditMapper;
import store.yd2team.business.service.CreditService;
import store.yd2team.business.service.CreditVO;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditMapper creditMapper;

    // 조회
    @Override
    public List<CreditVO> searchCredit(CreditVO vo) {
        return creditMapper.searchCredit(vo);
    }
    
    
    // 저장
    @Override
    public int saveCredit(CreditVO vo) throws Exception {
        System.out.println("### Service saveCredit 호출 ###");
        int result = creditMapper.insertCredit(vo);
        System.out.println("### result = " + result);
        return 1;   // 변화 건수 상관없이 성공 처리
    }


    // 조회 고객사 auto complete
    @Override
    public List<CreditVO> searchCustcomId(String keyword) {
    	return creditMapper.searchCustcomId(keyword);
    }
    
	@Override
	public List<CreditVO> searchCustcomName(String keyword) {
		return creditMapper.searchCustcomName(keyword);
	}



}
