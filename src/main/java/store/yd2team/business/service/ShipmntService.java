package store.yd2team.business.service;

import java.util.List;

public interface ShipmntService {

    // 출하 조회
    List<ShipmntVO> selectShipmntList(ShipmntVO vo);

    // 출하완료 처리 (다건)
   void completeShipment( String oustIds,
           String vendId,
           String empId,
           String loginId);

   
   // 출하취소
   void cancelShipment(
           String oustId,
           String cancelReason,
           String vendId,
           String userId
   );
//void cancelShipment(String oustId, String vendId, String empId, String loginId);

	/*
	 * void cancelShipment(String oustId, String vendId, String empId, String
	 * loginId);
	 */
    
	/*
	 * void completeShipment( String oustIdsCsv, String vendId, String empId, String
	 * loginId );
	 */
	/* void completeShipment(List<String> oustIds); */
}

