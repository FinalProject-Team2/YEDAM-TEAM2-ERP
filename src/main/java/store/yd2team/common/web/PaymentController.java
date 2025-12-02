package store.yd2team.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import store.yd2team.common.service.SubscriptionService;
import store.yd2team.common.service.subscriptionPlanVO;
import store.yd2team.common.util.LoginSession;

@Controller
public class PaymentController {
	
	@Autowired
	SubscriptionService subscriptionService;
	
	// 구독 결제 페이지로 이동 (SubscriptionChoice에서 POST)
	@PostMapping("/Payment")
	public String Payment(@RequestParam("planName") String planName,
			@RequestParam("billingCycle") String billingCycle,
			Model model) throws Exception {
		// DB에서 실제 플랜/주기에 따른 금액 조회
		subscriptionPlanVO plan = subscriptionService.getPlanForPayment(planName, billingCycle);
		long amount = (plan != null) ? plan.getAmt() : 0L;

		// 결제 주기 텍스트
		String billingCycleText = "MONTHLY".equalsIgnoreCase(billingCycle) ? "월간" : "연간";

		// 세션의 vendId로 tb_vend에서 상호명(vend_nm) 조회 (subscription 쪽 서비스 이용)
		String vendId = LoginSession.getVendId();
		String vendorName = subscriptionService.getVendNameById(vendId);
		if (vendorName == null || vendorName.isEmpty()) {
			vendorName = "알 수 없는 거래처";
		}

		// 주문명: 상호명 + 플랜명 + 결제주기
		String orderName = vendorName + " - " + planName + " (" + billingCycleText + ")";

		// 간단한 주문번호 생성
		String orderId = "order-" + System.currentTimeMillis();

		// 카드 요약에 표시할 값
		model.addAttribute("vendorName", vendorName);
		model.addAttribute("selectedPlanName", planName);
		model.addAttribute("billingCycleText", billingCycleText);
		model.addAttribute("amount", amount);

		// Toss 결제에 넘길 값
		model.addAttribute("orderId", orderId);
		model.addAttribute("orderName", orderName);
		model.addAttribute("customerEmail", "customer@example.com"); // TODO: 세션 이메일 연동
		model.addAttribute("customerName", vendorName);

		return "subscription/payment";
	}
	
	// Toss 결제 성공 콜백
	@GetMapping("/subscription/payment/success")
	public String paymentSuccess(
			@RequestParam(name = "orderId", required = false) String orderId,
			@RequestParam(name = "paymentKey", required = false) String paymentKey,
			@RequestParam(name = "amount", required = false) String amount,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("paymentResult", "success");
		redirectAttributes.addFlashAttribute("paymentOrderId", orderId);
		redirectAttributes.addFlashAttribute("paymentAmount", amount);

		return "subscription/success";
	}

	// Toss 결제 실패 콜백
	@GetMapping("/subscription/payment/fail")
	public String paymentFail(
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "message", required = false) String message,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("paymentResult", "fail");
		redirectAttributes.addFlashAttribute("paymentErrorCode", code);
		redirectAttributes.addFlashAttribute("paymentErrorMessage", message);

		return "subscription/fail";
	}

}// end class