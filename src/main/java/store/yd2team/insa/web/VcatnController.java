package store.yd2team.insa.web;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.VcatnService;
import store.yd2team.insa.service.VcatnVO;

@Controller
public class VcatnController {
	
	@Autowired VcatnService vcatnService;
	
	@GetMapping("/vcatn")
	public String vcatnRender(Model model) {
		
		;
		model.addAttribute("Session", LoginSession.getLoginSession());
		String id = LoginSession.getEmpId();
		model.addAttribute("remndrYryc", vcatnService.yrycUserRemndrChk(id) );
		return "insa/vcatn";

	}
	
	//다중입력조회
		@GetMapping("/vcatnJohoe")
		@ResponseBody
		public List<VcatnVO> edcJohoe(VcatnVO keyword) {		
				System.out.println(keyword);
				if(keyword.getConfmerId().equals("") ) {
					keyword.setConfmerId(null);
				}
				System.out.println("변경후"+keyword);
			return vcatnService.vcatnListVo(keyword);
		}		
		
		
	//휴가등록(연차소진)
			@PostMapping("/vcatnRegist")
			@ResponseBody
			public String vcatnRegistAdd(@RequestBody VcatnVO keyword) {
				System.out.println("모나오니"+keyword);				
				return vcatnService.vcatnRegist(keyword);
			}
			
	//휴가등록(연차소진)
			@PostMapping("/vcatnDel")
			@ResponseBody
			public String vcatnDelete(@RequestBody VcatnVO keyword) {
				System.out.println("모나오니"+keyword);				
				return vcatnService.vcatnRegist(keyword);
			}
			
	//휴가승인(관리자가 쓰는 메소드)
		@PostMapping("/vcatnUpdate")
		@ResponseBody
		public boolean vcatnUpdateEdit(@RequestBody VcatnVO keyword) {
			System.out.println("모나오니업데이트" + keyword);	
			
				return false;
		}
}
