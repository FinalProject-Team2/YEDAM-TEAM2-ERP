package store.yd2team.insa.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmpController {
	@GetMapping("/emp-register")
	public String empRender(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "insa/employee-register";

	}
}
