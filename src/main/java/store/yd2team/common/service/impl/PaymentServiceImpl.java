package store.yd2team.common.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.mapper.SignUpMapper;
import store.yd2team.common.mapper.SubscriptionMapper;
import store.yd2team.common.service.PaymentService;
import store.yd2team.common.service.SttlVO;
import store.yd2team.common.service.SubscriptionVO;
import store.yd2team.common.service.subscriptionPlanVO;
import store.yd2team.common.util.LoginSession;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
	
	@Autowired
	SubscriptionMapper subscriptionMapper;
	
	@Autowired
	SignUpMapper signUpMapper;
	
	/**
	 * 결제 성공 시 tb_subsp에 구독 정보 저장 (기존 있으면 UPDATE, 없으면 INSERT) 및 tb_sttl 결제내역 저장.
	 */
	@Override
	@Transactional
	public void saveSubscriptionOnPaymentSuccess(String planName, String billingCycle, Long amount, String paymentKey) throws Exception {
		// 1) 결제 이전에 선택했던 플랜 정보 조회 (요금제 ID 포함)
		subscriptionPlanVO plan = subscriptionMapper.selectPlanForPayment(planName, billingCycle);
		if (plan == null) {
			throw new IllegalStateException("해당 플랜 정보를 찾을 수 없습니다. planName=" + planName + ", billing=" + billingCycle);
		}
		
		// 2) 세션에서 로그인 사용자 정보 가져오기
		String vendId = LoginSession.getVendId();
		String empId = LoginSession.getEmpId();
		String empAcctId = LoginSession.getEmpAcctId(); // 추가

		if (vendId == null) {
			throw new IllegalStateException("로그인 세션 정보(vendId)가 없습니다.");
		}
		
		// 3) subsp_id 채번: subsp_YYYYMM + 3자리 시퀀스 (INSERT 시에만 사용)
		String yyyymm = new SimpleDateFormat("yyyyMM").format(new Date());
		String prefix = "subsp_" + yyyymm; // 예: subsp_202512
		String maxSeq = subscriptionMapper.getMaxSubspSeqOfMonth(prefix); // 예: "001"
		int nextSeqNum = 1;
		if (maxSeq != null && !maxSeq.isEmpty()) {
			nextSeqNum = Integer.parseInt(maxSeq) + 1;
		}
		String nextSeq = String.format("%03d", nextSeqNum);
		String subspId = prefix + nextSeq; // 예: subsp_202512001
		
		// 4) 시작일/종료일 계산 (업데이트/인서트 둘 다 공통)
		LocalDate startLocalDate = LocalDate.now();
		LocalDate endLocalDate = "YEARLY".equalsIgnoreCase(billingCycle)
				? startLocalDate.plusYears(1)
				: startLocalDate.plusMonths(1);
		java.sql.Date startDt = java.sql.Date.valueOf(startLocalDate);
		java.sql.Date endDt = java.sql.Date.valueOf(endLocalDate);
		
		// 5) vendId 기준 구독 존재 여부 확인 후 INSERT / UPDATE 분기
		int exists = subscriptionMapper.existsSubscriptionByVendId(vendId);
		SubscriptionVO vo = new SubscriptionVO();
		vo.setVendId(vendId);
		vo.setSubspPlanId(plan.getSubspPlanId());
		vo.setStartDt(startDt);
		vo.setEndDt(endDt);
		vo.setUpdtBy(empId);
		
		if (exists > 0) {
			// 이미 구독이 존재하면 UPDATE
			subscriptionMapper.updateSubscriptionByVendId(vo);
		} else {
			// 최초 구독이면 INSERT (ID 채번 사용)
			vo.setSubspId(subspId);
			vo.setAcctUseCnt(0L);
			vo.setApiUseCnt(0L);
			vo.setCreaBy(empId);
			subscriptionMapper.insertSubscription(vo);
		}
		
		// 6) tb_sttl INSERT (결제 이력)
		// sttl_id: sttl_YYYYMM + 3자리 시퀀스
		String sttlPrefix = "sttl_" + yyyymm;
		String maxSttlSeq = subscriptionMapper.getMaxSttlSeqOfMonth(sttlPrefix);
		int nextSttlNum = 1;
		if (maxSttlSeq != null && !maxSttlSeq.isEmpty()) {
			nextSttlNum = Integer.parseInt(maxSttlSeq) + 1;
		}
		String nextSttlSeq = String.format("%03d", nextSttlNum);
		String sttlId = sttlPrefix + nextSttlSeq;
		
		SttlVO sttl = new SttlVO();
		sttl.setSttlId(sttlId);
		sttl.setVendId(vendId);
		sttl.setSubspPlanId(plan.getSubspPlanId());
		sttl.setPaymentKey(paymentKey);
		sttl.setCreaBy(empId);
		
		subscriptionMapper.insertSttl(sttl);
		
		log.info("[PAYMENT] before role grant vendId={}, empId={}, empAcctId={}", vendId, empId, empAcctId);
		
		// ✅ 7) 결제 성공 시점에 마스터 권한(role) 부여
	    // 중복방지(없으면 insert)
	    int existsRole = signUpMapper.countUserRole(vendId, empAcctId, "ROLE_VEND_MASTER");
	    if (existsRole == 0) {
	        signUpMapper.insertUserRole(vendId, empAcctId, "ROLE_VEND_MASTER", empId);
	    }
	}

}// end class