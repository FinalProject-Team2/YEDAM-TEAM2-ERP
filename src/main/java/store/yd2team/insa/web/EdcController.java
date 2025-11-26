package store.yd2team.insa.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EdcController {
	//법정교육조회 관리자페이지 출력
	@GetMapping("/edc")
	public String edcRender(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "insa/edc";

	}
	
	
	//법정교육조회 사용자페이지 출력
	@GetMapping("/edcUser")
	public String edcUserRender(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "insa/edcUser";

	}
}
