package store.yd2team.insa.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
		
		//model.addAttribute("test", "testone");
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
}
