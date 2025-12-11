package store.yd2team.common.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import store.yd2team.common.dto.SttlHistoryDto;
import store.yd2team.common.dto.SubscriptionUsageDto;
import store.yd2team.common.service.SubscriptionService;
import store.yd2team.common.service.subscriptionPlanVO;
import store.yd2team.common.util.LoginSession;

@Controller
public class SubscriptionCheckController {
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	/**
	 * 내 구독정보 확인 페이지
	 */
	@GetMapping("/subscription/check")
	public String subscriptionCheck(Model model) throws Exception {
		// 로그인 세션에서 vendId 조회
		String vendId = LoginSession.getVendId();
		SubscriptionUsageDto usage = null;
		List<SttlHistoryDto> sttlHistoryList = java.util.Collections.emptyList();
		boolean canExtend = false; // 구독 연장 버튼 활성 여부
		if (vendId != null && !vendId.isEmpty()) {
			usage = subscriptionService.getSubscriptionUsageByVendId(vendId);
			sttlHistoryList = subscriptionService.getSttlHistoryByVendId(vendId);
			// tb_subsp.start_dt / end_dt 를 보고, 이미 만료된 경우에만 연장 가능
			if (usage != null && usage.getEndDt() != null) {
				LocalDate today = LocalDate.now();
				LocalDate end = usage.getEndDt().toLocalDate();
				// endDt < today 이면 이미 만료 → 연장 가능, 그 외(오늘 포함 아직 남아있음)는 연장 불가
				canExtend = end.isBefore(today);
			}
		}
		// 구독 정보 및 결제 내역이 없을 수도 있으므로 그대로 모델에 담기 (null/empty 허용)
		model.addAttribute("usage", usage);
		model.addAttribute("sttlHistoryList", sttlHistoryList);
		model.addAttribute("canExtend", canExtend);
		return "subscription/subscriptionCheck";
	}
	
	/**
	 * 구독 해지 (tb_subsp 테이블에서 삭제)
	 */
	@PostMapping("/subscription/cancel")
	public String cancelSubscription(RedirectAttributes redirectAttributes) throws Exception {
		String vendId = LoginSession.getVendId();
		if (vendId != null && !vendId.isEmpty()) {
			int deleted = subscriptionService.cancelSubscriptionByVendId(vendId);
			if (deleted > 0) {
				redirectAttributes.addFlashAttribute("subscriptionCancelMessage", "구독이 해지되었습니다.");
			} else {
				redirectAttributes.addFlashAttribute("subscriptionCancelMessage", "해지할 구독이 없습니다.");
			}
		} else {
			redirectAttributes.addFlashAttribute("subscriptionCancelMessage", "로그인 정보가 없습니다.");
		}
		return "redirect:/subscription/check";
	}
	
	/**
	 * 구독 연장: SubscriptionChoice 페이지를 생략하고, 현재 구독 중인 플랜으로 바로 결제 페이지로 이동
	 */
	@GetMapping("/subscription/extend")
	public String extendSubscription(RedirectAttributes redirectAttributes) throws Exception {
		String vendId = LoginSession.getVendId();
		if (vendId == null || vendId.isEmpty()) {
			redirectAttributes.addFlashAttribute("paymentResult", "fail");
			redirectAttributes.addFlashAttribute("paymentErrorMessage", "로그인 정보가 없습니다.");
			return "redirect:/subscription/check";
		}
		
		subscriptionPlanVO currentPlan = subscriptionService.getCurrentPlanByVendId(vendId);
		if (currentPlan == null) {
			redirectAttributes.addFlashAttribute("paymentResult", "fail");
			redirectAttributes.addFlashAttribute("paymentErrorMessage", "현재 활성화된 구독이 없습니다.");
			return "redirect:/subscription/check";
		}
		
		// sttlPerd(b1/b2/월/연 등)를 PaymentController가 기대하는 billingCycle 값(MONTHLY/YEARLY)으로 변환
		String sttlPerd = currentPlan.getSttlPerd();
		String billingCycle;
		if ("b1".equalsIgnoreCase(sttlPerd) || "월".equals(sttlPerd) || "MONTHLY".equalsIgnoreCase(sttlPerd)) {
			billingCycle = "MONTHLY";
		} else if ("b2".equalsIgnoreCase(sttlPerd) || "연".equals(sttlPerd) || "YEARLY".equalsIgnoreCase(sttlPerd)) {
			billingCycle = "YEARLY";
		} else {
			// 알 수 없는 주기값인 경우 기본을 MONTHLY로 처리
			billingCycle = "MONTHLY";
		}
		
		// PaymentController의 /Payment 핸들러로 리다이렉트하면서 planName, billingCycle 전달
		redirectAttributes.addAttribute("planName", currentPlan.getPlanNm());
		redirectAttributes.addAttribute("billingCycle", billingCycle);
		return "redirect:/Payment";
	}
	
}// end class