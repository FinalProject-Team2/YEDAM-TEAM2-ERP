package store.yd2team.common.service;

import java.util.List;

import store.yd2team.common.dto.SttlHistoryDto;
import store.yd2team.common.dto.SubscriptionUsageDto;

public interface SubscriptionService {
	
	// 구독 플랜 목록 조회
	List<subscriptionPlanVO> getSubscriptionPlans() throws Exception;
	
	// 구독 플랜 저장 (신규/수정 공통)
	int saveSubscriptionPlan(subscriptionPlanVO vo) throws Exception;
	
	// 결제용: 플랜명 + 결제주기로 단일 플랜 조회
	subscriptionPlanVO getPlanForPayment(String planNm, String billingCycle) throws Exception;
	
	// vendId(PK)로 상호명 조회 (tb_vend.vend_nm)
	String getVendNameById(String vendId) throws Exception;
	
	// 로그인 사용자의 구독 정보 + 사용량 조회
	SubscriptionUsageDto getSubscriptionUsageByVendId(String vendId) throws Exception;
	
	// 구독 해지: vendId 기준 tb_subsp 삭제
	int cancelSubscriptionByVendId(String vendId) throws Exception;
	
	// 결제 내역 조회
	List<SttlHistoryDto> getSttlHistoryByVendId(String vendId) throws Exception;
	
	// === 신규 추가: vendId 기준 현재 구독 플랜 조회 (tb_subsp.subsp_plan_id 기준) ===
	subscriptionPlanVO getCurrentPlanByVendId(String vendId) throws Exception;
	
}// end interface