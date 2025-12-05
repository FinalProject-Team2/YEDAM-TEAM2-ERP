package store.yd2team.insa.web;

import java.util.ArrayList;
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
import store.yd2team.insa.service.MlssService;
import store.yd2team.insa.service.MlssVO;

@Controller
public class MlssController {
	
	@Autowired MlssService mlssService;

	
	@GetMapping("/mlss")
	public String mlssRender(Model model) {
		
		if(LoginSession.getLoginSession() == null) {
			return "common/logIn";
		}
		
		int v = mlssService.mlssVisitChk( LoginSession.getEmpId() );
		if( v > 0) {	
			
		} else {
			model.addAttribute("mlss", "평가 기간이 아닙니다");
			return "index";
		}
		Map<String, List<MlssVO>> list = mlssService.mlssLoadBefore();
		
		model.addAttribute("iemList", list);
		model.addAttribute("Session", "testone");
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
		model.addAttribute("userInfo", userInfo);
		return "insa/mlss-regist-list";

	}
	
	//다면평가 등록
	@PostMapping("/mlssRegist")
	@ResponseBody
	public int vcatnDelete(@RequestBody MlssVO keyword) {		
		return mlssService.mlssRegist(keyword);	
	}
	
	//다중입력조회
		@GetMapping("/mlssListJohoe")
		@ResponseBody
		public List<MlssVO> mlssJohoe(MlssVO keyword) {			
			System.out.println("모나오니 다중입력조회"+keyword);				
			return mlssService.mlssListJohoe(keyword);
		}
	

}
