package store.yd2team.business.service;

import java.util.List;

public interface CustcomService {

	// 조회
	List<CustcomVO> searchCustcom(CustcomVO vo);
	
	// 저장
	int saveNewCust(CustcomVO vo) throws Exception;

	//공통코드 조회
	List<CustcomVO> getBSType();
	
	
}
