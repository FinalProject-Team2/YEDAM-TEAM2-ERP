package store.yd2team.business.service;

import java.util.List;

public interface PriceService {

	// 조회
	List<PriceVO> getPricePolicyList(PriceVO vo);
	
	// 공통코드 조회
    List<CommonCodeVO> getPriceType();
    
    // 등록 및 수정
    int savePricePolicy(PriceVO vo) throws Exception;
    
    // 삭제
    void deletePricePolicy(List<String> priceIdList) throws Exception;
}
