package store.yd2team.common.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
}// end class