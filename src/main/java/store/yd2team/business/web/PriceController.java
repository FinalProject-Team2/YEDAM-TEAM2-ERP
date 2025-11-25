package store.yd2team.business.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PriceController {

	@GetMapping("/price-manage")
	public String selectall(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "business/price-manage";

	}
}
