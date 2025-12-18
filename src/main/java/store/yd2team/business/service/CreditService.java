package store.yd2team.business.service;

import java.util.List;

public interface CreditService {
	
	// 검색조건(조회) : 여신한도 + 여신현황 리스트
	List<CreditVO> searchCredit(CreditVO vo);

	
	// 조회 고객사 auto complete(고객코드, 고객사명)
	List<CreditVO> searchCustcomId(String keyword);
	List<CreditVO> searchCustcomName(String keyword);
	
	// 업체정보 모달창
	CustcomVO getCustcomDetail(String custcomId);
	
	// 저장
	void saveCreditLimit(List<CreditVO> list);
	
	// 여신 회전일수 배치(batch) 실행
	void runTurnoverBatch();
	
	//여신등록
	/* int insertCdtlnLmt(CreditVO vo); */
	
	//미수등록
	int insertAtmpt(AtmptVO vo);
	
	//출하정지
	int updateShipmnt(CreditVO vo);
}



