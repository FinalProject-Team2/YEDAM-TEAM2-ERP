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
