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
		if(LoginSession.getLoginSession() == null) {
			return "common/logIn";
		}
		
		model.addAttribute("Session", LoginSession.getLoginSession());
		String id = LoginSession.getEmpId();
		model.addAttribute("remndrYryc", vcatnService.yrycUserRemndrChk(id) );
		return "insa/vcatn";

	}
	
	//다중입력조회
		@GetMapping("/vcatnJohoe")
		@ResponseBody
		public List<VcatnVO> edcJohoe(VcatnVO keyword) {	
				if(LoginSession.getMasYn().equals("e1") ) {
					keyword.setConfmerId("나는관리자");
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
			
	//휴가취소(연차신청롤백)
			@PostMapping("/vcatnDel")
			@ResponseBody
			public String vcatnDelete(@RequestBody VcatnVO keyword) {
				System.out.println("모나오니"+keyword);	
				int v = vcatnService.vcatnRollback(keyword);
				if (v >0) {
					return "휴가 취소 정상처리되었습니다.";					
				}
				return "휴가 취소가 처리되지 않았습니다.";	
			}
			
	//휴가승인(관리자가 쓰는 메소드)
		@PostMapping("/vcatnUpdate")
		@ResponseBody
		public String vcatnUpdateEdit(@RequestBody VcatnVO keyword) {
			System.out.println("모나오니업데이트" + keyword);	
			int v = vcatnService.vcatnCfmUpdate(keyword);
			if (v >0) {
				return "휴가 정상처리 되었습니다.";
			}
				return "휴가 정상처리 되지 않았습니다.";
		}
}
