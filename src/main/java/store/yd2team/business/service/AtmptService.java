package store.yd2team.business.service;
import java.util.List;
public interface AtmptService {
	
	// 검색조건(조회) : 여신한도 + 여신현황 리스트
	List<AtmptVO> searchAtmpt(AtmptVO vo);
	
	// 검색조건(저장)
	int saveAtmpt(AtmptVO vo) throws Exception;
	// 조회 고객사 auto complete(고객코드, 고객사명)
	List<AtmptVO> searchCustcomId(String keyword);
	List<AtmptVO> searchCustcomName(String keyword);
	
	
}

