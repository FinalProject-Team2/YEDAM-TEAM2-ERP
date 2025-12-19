package store.yd2team.insa.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.EmpService;
import store.yd2team.insa.service.EmpVO;

@Controller
public class OrgChartController {
	
	@Autowired EmpService empService;
	//test
			@GetMapping("/org")
			public String orgChartRender(Model model) {
				List<EmpVO> v = empService.getOrgRenderList( LoginSession.getVendId() );
				model.addAttribute("orgList", v);
				return "insa/orgtest";
			}
}
