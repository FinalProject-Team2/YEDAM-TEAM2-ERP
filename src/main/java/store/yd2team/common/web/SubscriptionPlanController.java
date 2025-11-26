package store.yd2team.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.common.service.SubscriptionService;

@Controller
public class SubscriptionPlanController {
    
	@Autowired
	SubscriptionService subscriptionService;
	
	// 구독 플랜 페에지
    @GetMapping("/SubscriptionPlan")
    public String SubscriptionPlan() {
        return "subscription/subscriptionPlan";
    }
    
}// end class