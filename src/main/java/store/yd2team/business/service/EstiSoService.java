package store.yd2team.business.service;

import java.util.List;

public interface EstiSoService {

	// 견적서 목록 조회
    List<EstiSoVO> selectEstiList(EstiSoVO cond);
    
    // 견적서 상태 업데이트
    int updateStatus(EstiSoVO vo);

    // 견적서 모달 상품 auto complete
    List<EstiSoVO> searchProduct(String keyword);
    EstiSoVO getProductDetail(String productId);
    
    // 견적서 모달 고객사 auto complete
    List<EstiSoVO> searchCustcomId(String keyword);
    List<EstiSoVO> searchCustcomName(String keyword);
    
}
