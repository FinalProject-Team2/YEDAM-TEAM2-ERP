package store.yd2team.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class loginPolicyController {
	
	@GetMapping("/loginPolicy")
	public String selectall(Model model) {
		model.addAttribute("test", "testone");
		return "common/loginPolicy";

	}
}
