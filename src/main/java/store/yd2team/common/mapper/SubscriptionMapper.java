package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.SubscriptionUsageDto;
import store.yd2team.common.service.subscriptionPlanVO;

@Mapper
public interface SubscriptionMapper {
	
	// 해당 월의 최대 시퀀스 3자리 조회
	String getMaxPlanSeqOfMonth(@Param("prefix") String prefix);
	
	// 구독 플랜 목록 조회
	List<subscriptionPlanVO> selectSubscriptionPlans();
	
	// 구독 플랜 등록
	int insertPlan(subscriptionPlanVO vo);
	
	// 구독 플랜 수정
	int updatePlan(subscriptionPlanVO vo);
	
	// 결제용: 플랜명 + 결제주기로 단일 플랜 조회
	subscriptionPlanVO selectPlanForPayment(@Param("planNm") String planNm,
			@Param("sttlPerd") String sttlPerd);
	
	// vendId(PK)로 tb_vend에서 상호명(vend_nm) 조회
	String selectVendNameById(@Param("vendId") String vendId);
	
	// 로그인 사용자의 vendId 기준 현재 구독 + 사용량 조회
	SubscriptionUsageDto selectSubscriptionUsageByVendId(@Param("vendId") String vendId);
	
}// end interface