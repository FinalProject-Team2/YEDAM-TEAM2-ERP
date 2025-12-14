package store.yd2team.business.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceVO;

@Mapper
public interface PriceMapper {

	// 조회
	List<PriceVO> selectPolicy(PriceVO searchVO);
	
	 // 공통코드 조회
    List<CommonCodeVO> selectPriceType();
    
    // 등록 및 수정
    int savePricePolicy(PriceVO vo);
    
    // 삭제
    void deletePricePolicy(List<String> priceIdList);
    
    // ========================= 고객사모달 저장버튼 이벤트
    // 단가정책 detail 저장
    int deletePriceDetail(String priceId);

    int insertPriceDetail(Map<String, Object> detail);

    // detail 조회
    List<Map<String, Object>> selectPricePolicyDetail(String priceId);
    
    // 상품모달 조회
    List<Map<String, Object>> selectProductList(String productName);
    
    // ========================= 상품모달 저장버튼 이벤트
    // 단가정책 상품 detail 조회
    List<Map<String, Object>> selectPricePolicyProduct(String priceId);

    // 기존 상품 detail 삭제
    int deletePriceProductDetail(String priceId);

    // 상품 detail 저장
    int insertPriceProductDetail(Map<String, Object> map);
    
    // 최대할인율
    PriceVO selectMaxDiscount(PriceVO vo);
}
