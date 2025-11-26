package store.yd2team.business.web;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.yd2team.business.service.BusinessVO;
@Controller
public class BusinessController {
//	@Autowired
//	BusinessService businessService;
	/*
	 * @GetMapping("/churnRiskStdrRegister") public String insert(Model model) {
	 *
	 * model.addAttri	bute("test", "testone"); return
	 * "/business/churnRiskStdrRegister"; // return "여기까지는 BusinessController 들어옴";
	 * }
	 */

	@GetMapping("/churnRiskStdrRegister")
	public String insert(Model model) {
	    System.out.println("=== BusinessController.insert() 호출됨 ===");
	    model.addAttribute("test", "testone");
	    return "business/churnRiskStdrRegister"; // /는 빼도 됨
	}
	
	@GetMapping("/churnRiskList")
	public String selectall(Model model) {
		System.out.println("=== BusinessController.selectall() 호출됨 ===");
		model.addAttribute("test", "testone");
		return "business/churnRiskList";
	}
}

