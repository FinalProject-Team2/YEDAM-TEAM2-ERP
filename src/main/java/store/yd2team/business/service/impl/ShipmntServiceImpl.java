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
    public void completeShipment(List<String> oustIdList) {

        String oustIds = String.join(",", oustIdList);

        shipmntMapper.procShipmentComplete(
            oustIds,
            LoginSession.getVendId(),
            LoginSession.getEmpId(),
            LoginSession.getLoginId()
        );
    }
}
