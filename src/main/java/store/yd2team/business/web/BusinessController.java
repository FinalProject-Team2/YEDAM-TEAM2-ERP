package store.yd2team.business.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.business.service.BusinessService;

@Controller
public class BusinessController {
//	@Autowired
//	BusinessService businessService;

	@GetMapping("/churnRiskStdrRegister")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "/business-churnRiskStdrRegister";
	}

}
