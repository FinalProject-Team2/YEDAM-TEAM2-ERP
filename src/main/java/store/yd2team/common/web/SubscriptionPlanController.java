package store.yd2team.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubscriptionPlanController {
    
    @GetMapping("/SubscriptionPlan")
    public String SubscriptionPlan() {
        // templates/subscription/subscription.html 을 의미
        return "subscription/subscriptionPlan";
    }
    
}// end class