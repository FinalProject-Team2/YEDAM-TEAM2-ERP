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
	// ✅ 여신 평가 (악성여신 / 출하정지 / 회전일수 등 갱신)
	//   - 반환값: 평가·업데이트된 고객사 수 (원하는 대로 int / void 등으로 바꿔도 됨)
	int evaluateCredit(CreditVO vo) throws Exception;
	
	// 저장
	void saveCreditLimit(List<CreditVO> list);
	
	//여신등록
	int insertCdtlnLmt(CreditVO vo);
	
	//미수등록
	int insertAtmpt(AtmptVO vo);
	
	//출하정지
	int updateShipmnt(CreditVO vo);
}



