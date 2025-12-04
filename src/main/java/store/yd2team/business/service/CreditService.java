package store.yd2team.business.service;

import java.util.List;

public interface CreditService {
	// 조회
	List<CreditVO> searchCredit(CreditVO vo);
	
	// 저장
	int saveCredit(CreditVO vo) throws Exception;

	// 조회 고객사 auto complete
	List<CreditVO> searchCustcomId(String keyword);
	List<CreditVO> searchCustcomName(String keyword);

	


}
