package store.yd2team.common.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.yd2team.common.dto.SttlHistoryDto;
import store.yd2team.common.dto.SubscriptionUsageDto;
import store.yd2team.common.mapper.SubscriptionMapper;
import store.yd2team.common.service.SubscriptionService;
import store.yd2team.common.service.subscriptionPlanVO;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
	
	@Autowired
	SubscriptionMapper subscriptionMapper;
	
	@Override
	@Transactional
	public int saveSubscriptionPlan(subscriptionPlanVO vo) throws Exception {
		// subspPlanId 유무로 신규/수정 분기
		if (vo.getSubspPlanId() == null || vo.getSubspPlanId().isEmpty()) {
			// 신규 등록: 오늘 기준 prefix(plan+yyyyMM)로 ID 생성 후 insert
			LocalDate today = LocalDate.now();
			String prefix = "plan" + '_' + today.format(DateTimeFormatter.ofPattern("yyyyMM"));
			
			String maxSeq = subscriptionMapper.getMaxPlanSeqOfMonth(prefix);
			int nextSeq = 1;
			if (maxSeq != null && !maxSeq.isEmpty()) {
				try {
					nextSeq = Integer.parseInt(maxSeq) + 1;
				} catch (NumberFormatException e) {
					nextSeq = 1;
				}
			}
			String seqStr = String.format("%03d", nextSeq);
			String generatedId = prefix + seqStr;
			
			vo.setSubspPlanId(generatedId);
			
			return subscriptionMapper.insertPlan(vo);
		} else {
			// 수정: 전달받은 subspPlanId 기준으로 update
			return subscriptionMapper.updatePlan(vo);
		}
	}
	
	@Override
	public List<subscriptionPlanVO> getSubscriptionPlans() throws Exception {
		return subscriptionMapper.selectSubscriptionPlans();
	}
	
	@Override
	public subscriptionPlanVO getPlanForPayment(String planNm, String billingCycle) throws Exception {
		// billingCycle(MONTHLY/YEARLY)를 Mapper에서 사용하는 sttlPerd 구분값으로 그대로 전달
		return subscriptionMapper.selectPlanForPayment(planNm, billingCycle);
	}
	
	@Override
	public String getVendNameById(String vendId) throws Exception {
		if (vendId == null || vendId.isEmpty()) {
			return null;
		}
		return subscriptionMapper.selectVendNameById(vendId);
	}
	
	@Override
	public SubscriptionUsageDto getSubscriptionUsageByVendId(String vendId) throws Exception {
		if (vendId == null || vendId.isEmpty()) {
			return null;
		}
		return subscriptionMapper.selectSubscriptionUsageByVendId(vendId);
	}
	
	@Override
	@Transactional
	public int cancelSubscriptionByVendId(String vendId) throws Exception {
		if (vendId == null || vendId.isEmpty()) {
			return 0;
		}
		return subscriptionMapper.deleteSubscriptionByVendId(vendId);
	}
	
	@Override
	public List<SttlHistoryDto> getSttlHistoryByVendId(String vendId) throws Exception {
		if (vendId == null || vendId.isEmpty()) {
			return java.util.Collections.emptyList();
		}
		return subscriptionMapper.selectSttlHistoryByVendId(vendId);
	}
	
	// === 신규 추가: vendId 기준 현재 구독 플랜 조회 ===
	@Override
	public subscriptionPlanVO getCurrentPlanByVendId(String vendId) throws Exception {
		if (vendId == null || vendId.isEmpty()) {
			return null;
		}
		return subscriptionMapper.selectCurrentPlanByVendId(vendId);
	}
	
}// end class