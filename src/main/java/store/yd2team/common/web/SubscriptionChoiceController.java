package store.yd2team.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubscriptionChoiceController {
	
	@GetMapping("/SubscriptionChoice")
	public String SubscriptionChoice() {
		// templates/subscription/subscription.html 을 의미
		return "subscription/subscriptionChoice";
	}

}// end class