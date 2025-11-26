package store.yd2team.insa.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MlssController {
	@GetMapping("/mlss")
	public String empRender(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "insa/mlss";

	}
	

}
