package store.yd2team.common.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.common.service.SubscriptionService;
import store.yd2team.common.service.subscriptionPlanVO;

@Controller
public class SubscriptionChoiceController {
	
	@Autowired
	SubscriptionService subscriptionService;
	
	// 구독 선택 페이지
	@GetMapping("/SubscriptionChoice")
	public String SubscriptionChoice(Model model) throws Exception {
		// DB에서 구독 플랜 목록 조회 후 모델에 담기
		List<subscriptionPlanVO> planList = subscriptionService.getSubscriptionPlans();
		model.addAttribute("planList", planList);
		// templates/subscription/subscription.html 을 의미
		return "subscription/subscriptionChoice";
	}

}// end class