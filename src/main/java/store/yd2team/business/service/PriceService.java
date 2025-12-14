package store.yd2team.business.service;

import java.util.List;
import java.util.Map;

public interface PriceService {

	// 조회
	List<PriceVO> getPricePolicyList(PriceVO vo);
	
	// 공통코드 조회
    List<CommonCodeVO> getPriceType();
    
    // 등록 및 수정
    int savePricePolicy(PriceVO vo) throws Exception;
    
    // 삭제
    void deletePricePolicy(List<String> priceIdList) throws Exception;
    
    // 고객사모달 저장버튼 이벤트
    int savePricePolicyDetail(PriceVO vo);

    List<Map<String, Object>> selectPricePolicyDetail(String priceId);
    
    
    // 상품모달 조회
    List<Map<String, Object>> selectProductList(String productName);
    
    // 상품모달 저장 이벤트
    List<Map<String, Object>> selectPricePolicyProduct(String priceId);

    int savePricePolicyProduct(PriceVO vo);
    
    // 최대 할인율
    PriceVO getMaxDiscount(PriceVO vo);
}
