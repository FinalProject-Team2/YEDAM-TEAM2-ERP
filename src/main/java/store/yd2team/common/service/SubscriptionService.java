package store.yd2team.common.service;

import java.util.List;

public interface SubscriptionService {
	
	// 구독 플랜 목록 조회
	List<subscriptionPlanVO> getSubscriptionPlans() throws Exception;
	
	// 구독 플랜 저장 (신규/수정 공통)
	int saveSubscriptionPlan(subscriptionPlanVO vo) throws Exception;
	
}// end interface