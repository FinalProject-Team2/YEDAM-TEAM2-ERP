package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.business.service.EstiSoDetailVO;
import store.yd2team.business.service.EstiSoVO;
import store.yd2team.business.service.OustVO;

@Mapper
public interface EstiSoMapper {

	 // 견적서 목록 조회
    List<EstiSoVO> selectEsti(EstiSoVO cond);
    
    // 견적서 그리드 상태 업데이트
    int updateStatus(EstiSoVO vo);
    
    // 견적서 모달 상품 auto complete
    List<EstiSoVO> searchProduct(@Param("keyword") String keyword);
    EstiSoVO getProductDetail(@Param("productId") String productId);
    
    // 모달 고객사 auto complete
    List<EstiSoVO> searchCustcomName(String keyword);  // 고객사명 검색
    List<EstiSoVO> searchCustcomId(String keyword);    // 고객사코드(아이디) 검색
    
    
    // 신규 견적서모달 저장버튼
    // 신규 견적서
    int insertNewEsti(EstiSoVO vo);

    // 견적서 수정 (이력)
    int insertEstiHistory(EstiSoVO vo);

    // 상세 저장 (항상 INSERT)
    int insertEstiDetail(EstiSoDetailVO detail);

    // 현재 버전 조회 (없으면 0)
    String selectCurrentVersion(@Param("estiId") String estiId);

    // 조회용
    EstiSoVO selectEstiHeader(@Param("estiId") String estiId);
    List<EstiSoDetailVO> selectEstiDetailList(@Param("estiId") String estiId);
    
    // 이력보기 모달
    List<EstiSoVO> selectEstiHistory(@Param("estiId") String estiId);
    
    //이력보기의 보기 모달
    EstiSoVO selectEstiHeaderByVersion(
	    @Param("estiId") String estiId,
	    @Param("version") String version
	);

	List<EstiSoDetailVO> selectEstiDetailListByVersion(
	    @Param("estiId") String estiId,
	    @Param("version") String version
	);
    
    
    // === 주문 관련 추가 ===
	void callCreateSoFromEsti(EstiSoVO vo);
	
	
    String createSoId();
    int insertSo(EstiSoVO vo);
    int insertSoDetail(EstiSoDetailVO vo);
    
    // 주문서등록 모달 저장버튼 클릭 시 견적 상태 es4 로 변경
    void updateEstiStatusToOrdered(
    	    @Param("estiId") String estiId,
    	    @Param("version") String version,
    	    @Param("updtBy") String updtBy
    	);
    
    // 주문 등록 시 출하예약수량 증가
    int increaseOustReserveQty(
        @Param("vendId") String vendId,
        @Param("productId") String productId,
        @Param("qty") Long qty,
        @Param("updtBy") String updtBy
    );
    
 // 주문 취소 시 출하예약수량 증가
    int decreaseOustReserveQty(
        @Param("vendId") String vendId,
        @Param("productId") String productId,
        @Param("qty") Long qty,
        @Param("updtBy") String updtBy
    );
    
    
    
    // ==================================================== 주문서관리
    // 주문서 조회
    List<EstiSoVO> selectSoHeaderList(EstiSoVO so);
    List<EstiSoDetailVO> selectSoDetailList(String soId);
    
    // 주문서관리화면 승인버튼
    EstiSoVO getOrderHeader(@Param("soId") String soId);

    String selectSoStatus(@Param("soId") String soId);

    void updateSoStatusToApproved(
    	    @Param("soId") String soId,
    	    @Param("vendId") String vendId,
    	    @Param("updtBy") String updtBy
    	);
	/* int updateSoStatusToApproved(String soId); */
    
    // 보류버튼 이벤트
    void updateRejectStatus(
        @Param("soId") String soId,
        @Param("reason") String reason,
        @Param("vendId") String vendId,
        @Param("updtBy") String updtBy
    );
    
    // 주문취소버튼 이벤트
    void updateCancelStatus(
        @Param("soId") String soId,
        @Param("reason") String reason,
        @Param("vendId") String vendId,
        @Param("updtBy") String updtBy
    );
    
    // 주문취소 시 출하예약수량 감소
    int decreaseOustReserveQty(
	    @Param("vendId") String vendId,
	    @Param("productId") String productId,
	    @Param("qty") long qty,
	    @Param("updtBy") String updtBy
	); 
   
    
    // 출하지시서 작성 저장 버튼
    void insertOust(OustVO vo);
    void updateSoStatus(
            @Param("soId") String soId,
            @Param("status") String status,
            @Param("vendId") String vendId,
            @Param("updtBy") String updtBy
        );

}
