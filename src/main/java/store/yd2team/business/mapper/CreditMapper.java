package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import store.yd2team.business.service.AtmptVO;
import store.yd2team.business.service.CreditVO;
import store.yd2team.business.service.CustcomVO;

public interface CreditMapper {
	//검색조건(조회)
	List<CreditVO> searchCredit(CreditVO searchVO);
	
	// 조회 고객사 auto complete(고객코드, 고객사명)
	List<CreditVO> searchCustcomId(String keyword);    // 고객사코드(아이디) 검색
	List<CreditVO> searchCustcomName(String keyword);  // 고객사명 검색
	
	//업체정보 모달창
	CustcomVO selectCustcomDetail(String custcomId);

	//단건조회
	CreditVO selectCreditStatus(CreditVO target);
	
	// 저장버튼 이벤트
	int updateCreditLimit(CreditVO vo);
	
	// 여신 회전일수 배치(batch) 실행
	int updateTurnoverAndPolicyBatch(@Param("updtBy") String updtBy);
	
	
	//미수등록
	int insertAtmpt(AtmptVO vo);
	
	//출하정지
	int updateShipmnt(CreditVO vo);
	
	
}



