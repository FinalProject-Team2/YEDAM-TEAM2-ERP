package store.yd2team.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmpController {
	@GetMapping("/emp-register")
	public String selectall(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "employee-register";
	}
}
