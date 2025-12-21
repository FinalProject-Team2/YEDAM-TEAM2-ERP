package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.ShipmntMapper;
import store.yd2team.business.service.ShipmntService;
import store.yd2team.business.service.ShipmntVO;
import store.yd2team.common.util.LoginSession;

@Service
@RequiredArgsConstructor
public class ShipmntServiceImpl implements ShipmntService {
	
	private final ShipmntMapper shipmntMapper;

	// 견적서 조회
    @Override
    public List<ShipmntVO> selectShipmntList(ShipmntVO cond) {
        return shipmntMapper.selectShipmnt(cond);
    }

	/*
	 * // 출하처리
	 * 
	 * @Override
	 * 
	 * @Transactional public void completeShipment(List<String> oustIds) {
	 * 
	 * // 1. 유효성 체크 if (oustIds == null || oustIds.isEmpty()) { return; }
	 * 
	 * // 2. 출하상태 업데이트 shipmntMapper.updateShipmntComplete(oustIds); }
	 */
    
    //출하처리
    @Override
    @Transactional
    public void completeShipment( 
    		String oustIds,
            String vendId,
            String empId,
            String loginId) {

        // List → "OU1,OU2,OU3" 형태로 변환
        String oustIdCsv = String.join(",", oustIds);

        shipmntMapper.procShipmentComplete(
            oustIdCsv,
            LoginSession.getVendId(),
            LoginSession.getEmpId(),
            LoginSession.getLoginId()
        );
    }

    // 출하취소
    @Transactional
    @Override
    public void cancelShipment(
            String oustId,
            String cancelReason,
            String vendId,
            String userId
    ) {
        // 출하상태 (oust)
        shipmntMapper.updateOustShipmntCancel(
                oustId,
                cancelReason,
                userId
        );

        // 출하상태 (shipmnt)
        shipmntMapper.updateShipmntCancel(
                oustId,
                cancelReason,
                userId
        );

        // so_id 조회
        String soId = shipmntMapper.selectSoIdByOustId(oustId);
        if (soId == null) {
            throw new IllegalStateException("so_id 없음 : " + oustId);
        }

        // 주문상세 상품별 수량 조회
        List<ShipmntVO> list =
                shipmntMapper.selectSoDetailQtyList(soId);

        // 상품별 출하예약수량 원복
        for (ShipmntVO vo : list) {
            shipmntMapper.rollbackReserveQty(
                    vendId,
                    vo.getProductId(),
                    vo.getShipQty(),
                    userId
            );
        }
    }
    
	/*
	 * @Override
	 * 
	 * @Transactional public void cancelShipment( String oustId, String
	 * cancelReason, String vendId, String userId ) {
	 * 
	 * // 1. 출하 상태 업데이트 (출하취소 + 사유) shipmntMapper.updateShipmntCancel( oustId,
	 * cancelReason, userId );
	 * 
	 * // 2. so_id 조회 String soId = shipmntMapper.selectSoIdByOustId(oustId); if
	 * (soId == null) { throw new IllegalStateException("so_id 없음 : " + oustId); }
	 * 
	 * // 3. 주문상세 상품별 수량 조회 List<ShipmntVO> detailList =
	 * shipmntMapper.selectSoDetailQtyList(soId);
	 * 
	 * // 4. 상품별 출하예약수량 원복 for (ShipmntVO vo : detailList) {
	 * shipmntMapper.rollbackReserveQty( vendId, vo.getProductId(), vo.getShipQty(),
	 * userId ); } }
	 */
}
