package store.yd2team.insa.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttdController {
	@GetMapping("/attd")
	public String selectall(Model model) {
		model.addAttribute("test", "testone");
		return "/insa/attd";
	}
}
