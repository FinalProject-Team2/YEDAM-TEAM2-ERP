package store.yd2team.insa.web;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import store.yd2team.insa.service.EmpService;
import store.yd2team.insa.service.EmpVO;

@Controller
public class EmpController {
	
	@Autowired EmpService empService;
	
	@GetMapping("/emp-register")
	public String empRender(Model model) {
		
		
		
		
		model.addAttribute("test", "testone");
		return "insa/employee-register";

	}
	
	@GetMapping("/empJohoe")
	@ResponseBody
	public List<EmpVO> empJohoe(@RequestParam("nm") String name, 
			               @RequestParam("empId") String empId, 
			               @RequestParam("deptNm") String deptNm, 
			               @RequestParam("clsf") String clsf) {
		EmpVO johoeKeyword = new EmpVO();
		johoeKeyword.setNm(deptNm);
		johoeKeyword.setEmpId(empId);
		johoeKeyword.setDeptId(deptNm);
		johoeKeyword.setClsf(clsf);		
		
		return empService.getListEmpJohoe(johoeKeyword);
	}
}
