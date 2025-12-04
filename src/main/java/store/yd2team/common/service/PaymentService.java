package store.yd2team.common.service;

public interface PaymentService {
	
	/**
	 * 결제 성공 시 tb_subsp에 구독 정보를 저장한다.
	 *
	 * @param planName      SubscriptionChoice에서 선택한 플랜명
	 * @param billingCycle  결제 주기(MONTHLY/YEARLY)
	 * @param amount        결제 금액(검증용, 필요 시 활용)
	 * @throws Exception    저장 중 에러 발생 시
	 */
	void saveSubscriptionOnPaymentSuccess(String planName, String billingCycle, Long amount) throws Exception;

}// end interface