package store.yd2team.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import store.yd2team.common.service.PaymentService;
import store.yd2team.common.service.SubscriptionService;
import store.yd2team.common.service.subscriptionPlanVO;
import store.yd2team.common.util.LoginSession;
import store.yd2team.common.util.LoginSessionRefresher;

@Controller
public class PaymentController {
	
	@Autowired
	LoginSessionRefresher loginSessionRefresher;
	
	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	PaymentService paymentService;
	
	// 공통: 결제 페이지 세팅 로직 (GET/POST 둘 다에서 사용)
	private String setupPaymentPage(String planName,
			String billingCycle,
			Model model,
			HttpSession session) throws Exception {
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

		// 결제 성공 시 다시 사용할 플랜명/주기/금액을 세션에 저장
		session.setAttribute("PAYMENT_PLAN_NAME", planName);
		session.setAttribute("PAYMENT_BILLING_CYCLE", billingCycle);
		session.setAttribute("PAYMENT_AMOUNT", amount);

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
	
	// 구독 결제 페이지로 이동 (SubscriptionChoice에서 POST)
	@PostMapping("/Payment")
	public String Payment(@RequestParam("planName") String planName,
			@RequestParam("billingCycle") String billingCycle,
			Model model,
			HttpSession session) throws Exception {
		return setupPaymentPage(planName, billingCycle, model, session);
	}
	
	// 구독 연장 등에서 GET으로 직접 진입 (예: /subscription/extend → redirect:/Payment)
	@GetMapping("/Payment")
	public String PaymentGet(@RequestParam("planName") String planName,
			@RequestParam("billingCycle") String billingCycle,
			Model model,
			HttpSession session) throws Exception {
		return setupPaymentPage(planName, billingCycle, model, session);
	}
	
	// Toss 결제 성공 콜백
	@GetMapping("/subscription/payment/success")
	public String paymentSuccess(
			@RequestParam(name = "orderId", required = false) String orderId,
			@RequestParam(name = "paymentKey", required = false) String paymentKey,
			@RequestParam(name = "amount", required = false) Long amount,
			HttpSession session,
			RedirectAttributes redirectAttributes) throws Exception {

		// 세션에 저장해둔 결제 관련 정보 조회
		String planName = (String) session.getAttribute("PAYMENT_PLAN_NAME");
		String billingCycle = (String) session.getAttribute("PAYMENT_BILLING_CYCLE");
		Long sessionAmount = (Long) session.getAttribute("PAYMENT_AMOUNT");
		
		// amount 파라미터와 세션 값이 다를 경우를 대비해 우선순위는 파라미터 > 세션
		Long finalAmount = (amount != null ? amount : sessionAmount);

		// 결제 성공 시 tb_subsp / tb_sttl 저장 (paymentKey 포함)
		paymentService.saveSubscriptionOnPaymentSuccess(planName, billingCycle, finalAmount, paymentKey);

		// 세션에 저장해둔 정보는 더 이상 필요 없으므로 제거
		session.removeAttribute("PAYMENT_PLAN_NAME");
		session.removeAttribute("PAYMENT_BILLING_CYCLE");
		session.removeAttribute("PAYMENT_AMOUNT");

		// 알림 메시지와 함께 /subscription/check 로 이동시키기 위해 Flash Attribute 사용
		redirectAttributes.addFlashAttribute("paymentSuccessMessage", "결제가 정상적으로 완료됐습니다");

		String empAcctId = LoginSession.getEmpAcctId();
		if (empAcctId != null) {
		    loginSessionRefresher.refresh(session, empAcctId);
		}

		// 최종적으로 /subscription/check로 리다이렉트
		return "redirect:/subscription/check";
		
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