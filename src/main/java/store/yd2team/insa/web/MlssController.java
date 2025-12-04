package store.yd2team.insa.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.common.util.LoginSession;

@Controller
public class MlssController {
	@GetMapping("/mlss")
	public String mlssRender(Model model) {
		
		String empId = LoginSession.getEmpId();
		String vendId = LoginSession.getVendId();
		String deptId = LoginSession.getDeptId();
		
		
		model.addAttribute("test", "testone");
		return "insa/mlss";

	}
	
	@GetMapping("/mlss/master")
	public String mlssMasterRender(Model model) {
		
		String empId = LoginSession.getEmpId();
		String vendId = LoginSession.getVendId();
		String deptId = LoginSession.getDeptId();
		List<String> userInfo = new ArrayList<>();
		userInfo.add(empId);
		userInfo.add(vendId);
		userInfo.add(deptId);
		System.out.println("모나오니"+userInfo);
		model.addAttribute("userInfo", userInfo);
		return "insa/mlss-regist-list";

	}
	

}
