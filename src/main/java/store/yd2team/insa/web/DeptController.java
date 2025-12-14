package store.yd2team.insa.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.DeptService;
import store.yd2team.insa.service.DeptVO;

@Controller
public class DeptController {

	@Autowired DeptService deptService;
	
	//법정교육조회 관리자페이지 출력
		@GetMapping("/dept")
		public String deptRender(Model model) {	
			Map<String, Object> result = new HashMap<>();
			String vendId = LoginSession.getVendId();
			List<DeptVO> vo = deptService.getListDept(vendId);
			List<DeptVO> nonManagerData = deptService.getNonManagerEmployeeIds(vendId);
		    
		    model.addAttribute("deptVOList", vo);		
		    model.addAttribute("nonManagerData", nonManagerData);		
					
			return "insa/dept";

		}
		
	//부서 관리 머지문(매치시 업뎃 미스매치시 인서트) 
		@PostMapping("/mergeDept")
		@ResponseBody
		public int mergeDept(@RequestBody List<DeptVO> keyword) {
			System.out.println("모나오니"+keyword);			
			return deptService.mergeDept(keyword);
		}		
		
	//부서 삭제할 때 쓰는 기능 
		@PostMapping("/deleteDept")
		@ResponseBody
		public int deleteDept(@RequestBody List<DeptVO> keyword) {
			System.out.println("모나오니"+keyword);			
			return deptService.deleteDept(keyword);
		}
}
