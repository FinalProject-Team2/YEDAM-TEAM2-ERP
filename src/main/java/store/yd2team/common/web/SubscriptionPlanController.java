package store.yd2team.common.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import store.yd2team.common.service.SubscriptionService;
import store.yd2team.common.service.subscriptionPlanVO;

@Controller
@RequestMapping("/subscription")
public class SubscriptionPlanController {
    
	@Autowired
	SubscriptionService subscriptionService;
	
	// 구독 플랜 페이지
    @GetMapping("/plan")
    public String subscriptionPlanForm(Model model) throws Exception {
    	// DB에서 구독 플랜 목록 조회 후 모델에 담기
    	List<subscriptionPlanVO> planList = subscriptionService.getSubscriptionPlans();
    	model.addAttribute("planList", planList);
        return "subscription/subscriptionPlan";
    }
    
    // 구독 플랜 저장
    @PostMapping("/plan/save")
    public String saveSubscriptionPlan(@ModelAttribute subscriptionPlanVO vo) throws Exception {
    	// 단순히 저장 후 목록/화면으로 리다이렉트 (추후 메시지 처리 가능)
    	subscriptionService.saveSubscriptionPlan(vo);
    	return "redirect:/subscription/plan";
    }
    
}// end class