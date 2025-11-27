package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
	
}// end interface