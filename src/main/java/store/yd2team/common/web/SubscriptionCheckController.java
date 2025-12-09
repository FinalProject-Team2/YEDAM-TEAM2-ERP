package store.yd2team.common.web;

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
		if (vendId != null && !vendId.isEmpty()) {
			usage = subscriptionService.getSubscriptionUsageByVendId(vendId);
			sttlHistoryList = subscriptionService.getSttlHistoryByVendId(vendId);
		}
		// 구독 정보 및 결제 내역이 없을 수도 있으므로 그대로 모델에 담기 (null/empty 허용)
		model.addAttribute("usage", usage);
		model.addAttribute("sttlHistoryList", sttlHistoryList);
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
	
}// end class