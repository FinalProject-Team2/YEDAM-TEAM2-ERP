package store.yd2team.common.service;

public interface PaymentService {
	
	/**
	 * 결제 성공 시 tb_subsp 및 tb_sttl에 구독/결제 정보를 저장한다.
	 *
	 * @param planName      SubscriptionChoice에서 선택한 플랜명
	 * @param billingCycle  결제 주기(MONTHLY/YEARLY)
	 * @param amount        결제 금액(검증용, 필요 시 활용)
	 * @param paymentKey    결제 성공 시 Toss에서 전달된 paymentKey
	 * @throws Exception    저장 중 에러 발생 시
	 */
	void saveSubscriptionOnPaymentSuccess(String planName, String billingCycle, Long amount, String paymentKey) throws Exception;

}// end interface