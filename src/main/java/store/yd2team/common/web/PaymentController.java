package store.yd2team.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentController {
	
	// 결제 페이지
	@GetMapping("/Payment")
	public String Payment() {
		return "subscription/payment";
	}
	
	// Toss 결제 성공 콜백: 토스가 successUrl로 리다이렉트할 때 도착하는 엔드포인트
	@GetMapping("/subscription/payment/success")
	public String paymentSuccess(
			@RequestParam(name = "orderId", required = false) String orderId,
			@RequestParam(name = "paymentKey", required = false) String paymentKey,
			@RequestParam(name = "amount", required = false) String amount,
			RedirectAttributes redirectAttributes) {

		// TODO: 실제 서비스에서는 여기서 Toss 결제 승인 API를 호출해 결금을 최종 확정합니다.
		// 이 예제에서는 흐름만 잡고, 간단한 성공 메시지만 세팅합니다.
		redirectAttributes.addFlashAttribute("paymentResult", "success");
		redirectAttributes.addFlashAttribute("paymentOrderId", orderId);
		redirectAttributes.addFlashAttribute("paymentAmount", amount);

		// 단순히 성공 페이지로 이동
		return "subscription/success";
	}

	// Toss 결제 실패 콜백: 토스가 failUrl로 리다이렉트할 때 도착하는 엔드포인트
	@GetMapping("/subscription/payment/fail")
	public String paymentFail(
			@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "message", required = false) String message,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("paymentResult", "fail");
		redirectAttributes.addFlashAttribute("paymentErrorCode", code);
		redirectAttributes.addFlashAttribute("paymentErrorMessage", message);

		// 단순히 실패 페이지로 이동
		return "subscription/fail";
	}

}// end class