package store.yd2team.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.common.util.LoginSession;

@Controller
public class SampleController {
	//test
		@GetMapping("/sam")
		public String selectall(Model model) {
			
			
			
			
			model.addAttribute("test", LoginSession.getLoginSession());
			return "index";
		}
}
