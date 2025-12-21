package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.business.service.ShipmntVO;

@Mapper
public interface ShipmntMapper {
	
	// 견적서 목록 조회
    List<ShipmntVO> selectShipmnt(ShipmntVO cond);
    
    
	/*
	 * //출하처리 업데이트 void updateShipmntComplete(List<String> oustIds);
	 */

    //출하처리 프로시져
    void procShipmentComplete(
            @Param("oustIds") String oustIds,
            @Param("vendId")  String vendId,
            @Param("empId")   String empId,
            @Param("loginId") String loginId
        );
    
    // ==============================================================  출하취소
 // 출하취소 상태 업데이트
    void updateOustShipmntCancel(
	    @Param("oustId") String oustId,
	    @Param("cancelReason") String cancelReason,
	    @Param("userId") String userId
	);

	void updateShipmntCancel(
	    @Param("oustId") String oustId,
	    @Param("cancelReason") String cancelReason,
	    @Param("userId") String userId
	);

    // oust_id → so_id 조회
    String selectSoIdByOustId(
            @Param("oustId") String oustId
    );

    // 주문상세 상품별 수량 조회 (tb_so_detail.qy)
    List<ShipmntVO> selectSoDetailQtyList(
            @Param("soId") String soId
    );

    // 출하예약수량 원복
    void rollbackReserveQty(
            @Param("vendId") String vendId,
            @Param("productId") String productId,
            @Param("shipQty") Integer shipQty,
            @Param("userId") String userId
    );
    
	/*
	 * void procShipmentComplete(
	 * 
	 * @Param("oustIds") String oustIds,
	 * 
	 * @Param("vendId") String vendId,
	 * 
	 * @Param("empId") String empId,
	 * 
	 * @Param("loginId") String loginId );
	 */

	
}
