package store.yd2team.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
		if (vendId != null && !vendId.isEmpty()) {
			usage = subscriptionService.getSubscriptionUsageByVendId(vendId);
		}
		// 구독 정보가 없을 수도 있으므로 그대로 모델에 담기 (null 허용)
		model.addAttribute("usage", usage);
		return "subscription/subscriptionCheck";
	}
	
}// end class