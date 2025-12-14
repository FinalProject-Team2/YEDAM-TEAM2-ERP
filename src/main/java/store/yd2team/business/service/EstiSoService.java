package store.yd2team.business.service;

import java.util.List;
import java.util.Map;

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
    
    
    
    /** 저장 (항상 INSERT, 이력 관리) */
    String saveEsti(EstiSoVO vo);

    /** 최신버전 기준 조회 (필요시 버전별 조회는 추가로 만들면 됨) */
    EstiSoVO getEsti(String estiId);
    
    // 이력보기 모달
    List<EstiSoVO> getEstiHistory(String estiId);
    
    // 이력보기의 보기 모달
    EstiSoVO getEstiByVersion(String estiId, String version);
    
    // 주문서 모달 초기 데이터 (견적 기반)
    EstiSoVO getOrderInitFromEsti(String estiId);

    // 견적 → 주문 저장
    String saveOrderFromEsti(EstiSoVO vo);
    
    // ========================================================= 주문서 관리
    // 주문서 목록 조회
    List<EstiSoVO> selectSoList(EstiSoVO so);
    
    // 주문서관리화면 승인버튼
    void approveSo(List<EstiSoVO> list) throws Exception;
    
    // 보류버튼 이벤트
    Map<String, Object> rejectOrder(String soId, String reason);
    
    // 주문취소버튼 이벤트
    Map<String, Object> cancelOrder(String soId, String reason);
    
    // 출하지시서 작성 저장 버튼
    void saveOust(OustVO vo) throws Exception;
}
