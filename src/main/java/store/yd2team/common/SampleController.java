package store.yd2team.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {
	//test
		@GetMapping("/")
		public String selectall(Model model) {
			
			
			
			
			model.addAttribute("test", "testone");
			return "index";
		}
}
