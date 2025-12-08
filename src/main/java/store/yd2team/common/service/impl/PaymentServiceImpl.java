package store.yd2team.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.yd2team.common.mapper.SubscriptionMapper;
import store.yd2team.common.service.PaymentService;
import store.yd2team.common.service.SubscriptionVO;
import store.yd2team.common.service.subscriptionPlanVO;
import store.yd2team.common.util.LoginSession;

@Service
public class PaymentServiceImpl implements PaymentService {
	
	@Autowired
	SubscriptionMapper subscriptionMapper;
	
	/**
	 * 결제 성공 시 tb_subsp에 구독 정보 저장.
	 */
	@Override
	@Transactional
	public void saveSubscriptionOnPaymentSuccess(String planName, String billingCycle, Long amount) throws Exception {
		// 1) 결제 이전에 선택했던 플랜 정보 조회 (요금제 ID 포함)
		subscriptionPlanVO plan = subscriptionMapper.selectPlanForPayment(planName, billingCycle);
		if (plan == null) {
			throw new IllegalStateException("해당 플랜 정보를 찾을 수 없습니다. planName=" + planName + ", billing=" + billingCycle);
		}
		
		// 2) 세션에서 로그인 사용자 정보 가져오기
		String vendId = LoginSession.getVendId();
		String empId = LoginSession.getEmpId();
		if (vendId == null) {
			throw new IllegalStateException("로그인 세션 정보(vendId)가 없습니다.");
		}
		
		// 3) subsp_id 채번: subsp_YYYYMM + 3자리 시퀀스
		String yyyymm = new SimpleDateFormat("yyyyMM").format(new Date());
		String prefix = "subsp_" + yyyymm; // 예: subsp_202512
		String maxSeq = subscriptionMapper.getMaxSubspSeqOfMonth(prefix); // 예: "001"
		int nextSeqNum = 1;
		if (maxSeq != null && !maxSeq.isEmpty()) {
			nextSeqNum = Integer.parseInt(maxSeq) + 1;
		}
		String nextSeq = String.format("%03d", nextSeqNum);
		String subspId = prefix + nextSeq; // 예: subsp_202512001
		
		// 4) VO 구성 후 INSERT
		SubscriptionVO vo = new SubscriptionVO();
		vo.setSubspId(subspId);
		vo.setVendId(vendId);
		vo.setSubspPlanId(plan.getSubspPlanId());
		vo.setAcctUseCnt(0L);
		vo.setApiUseCnt(0L);
		vo.setCreaBy(empId);
		
		subscriptionMapper.insertSubscription(vo);
	}

}// end class