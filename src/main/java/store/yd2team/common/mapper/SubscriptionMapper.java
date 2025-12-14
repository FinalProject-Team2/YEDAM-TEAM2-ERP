package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.SttlHistoryDto;
import store.yd2team.common.dto.SubscriptionUsageDto;
import store.yd2team.common.service.SttlVO;
import store.yd2team.common.service.SubscriptionVO;
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
	
	// === 신규 추가: 결제 내역 조회 ===
	List<SttlHistoryDto> selectSttlHistoryByVendId(@Param("vendId") String vendId);
	
	// === 신규 추가: 구독(tb_subsp) ID 채번 및 등록 ===
	// 현재 연월 기준 최대 구독 ID 시퀀스 조회 (예: subsp_202512001 중 001 부분)
	String getMaxSubspSeqOfMonth(@Param("prefix") String prefix);
	
	// 구독(tb_subsp) 등록
	int insertSubscription(SubscriptionVO vo);
	
	// 신규 추가: vendId 기준 tb_subsp 구독 삭제 (구독 해지)
	int deleteSubscriptionByVendId(@Param("vendId") String vendId);
	
	// === 정산/결제(tb_sttl) 관련 ===
	// 현재 연월 기준 최대 결제 ID 시퀀스 조회 (예: sttl_202512001 중 001 부분)
	String getMaxSttlSeqOfMonth(@Param("prefix") String prefix);
	
	// 결제(tb_sttl) 정보 등록
	int insertSttl(SttlVO vo);
	
	// === 정산/결제 관련: vendId로 구독 존재 여부 체크 ===
	// vendId로 tb_subsp에서 구독 존재 여부 체크 (1: 존재, 0: 미존재)
	int existsSubscriptionByVendId(@Param("vendId") String vendId);
	
	// vendId 기준 tb_subsp 구독 정보 수정
	int updateSubscriptionByVendId(SubscriptionVO vo);
	
	// === 신규 추가: vendId 기준 현재 구독 플랜 조회 (tb_subsp.subsp_plan_id -> tb_subsp_plan 조인) ===
	subscriptionPlanVO selectCurrentPlanByVendId(@Param("vendId") String vendId);
	
	// === 신규 추가: API 사용량 증가 (tb_subsp.api_use_cnt = api_use_cnt + 1) ===
	int incrementApiUseCountByVendId(@Param("vendId") String vendId);
	
	// 만료인 계정 r4로 변경
	List<String> selectExpiredVendIds();
	
	// 결제 안 한 계정 확인
	int countActiveSubsp(@Param("vendId") String vendId);

	
}// end interface