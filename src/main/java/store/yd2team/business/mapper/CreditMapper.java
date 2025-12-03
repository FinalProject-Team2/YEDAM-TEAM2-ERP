package store.yd2team.business.mapper;

import java.util.List;

import store.yd2team.business.service.CreditVO;

public interface CreditMapper {
	// 조회
	List<CreditVO> searchCredit(CreditVO searchVO);
	 
	// 저장 
	int insertCredit(CreditVO vo);
	
	// 조회 auto complete.
	List<CreditVO> searchCustcomId(String keyword);    // 고객사코드(아이디) 검색
	List<CreditVO> searchCustcomName(String keyword);  // 고객사명 검색

}
