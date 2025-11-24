package store.yd2team.salary.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SalyController {
	@GetMapping("/salyLedg")
	public String selectall(Model model) {
		model.addAttribute("test", "testone");
		return "/saly/salyLedg";
	}
}
