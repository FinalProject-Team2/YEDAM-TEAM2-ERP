package store.yd2team.insa.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import store.yd2team.common.web.SysLogController;
import store.yd2team.insa.service.EdcService;
import store.yd2team.insa.service.EdcVO;

@Controller
public class EdcController {

    private final SysLogController sysLogController;
	
	@Autowired EdcService edcService;

    EdcController(SysLogController sysLogController) {
        this.sysLogController = sysLogController;
    }
	
	//법정교육조회 관리자페이지 출력
	@GetMapping("/edc")
	public String edcRender(Model model) {	
		Map<String, Object> optionMap = edcService.getInputOption();
		
	    model.addAttribute("dept", optionMap.get("dept"));   // List<DeptVO>
	    model.addAttribute("grade", optionMap.get("grade")); // List<CodeVO>
	    model.addAttribute("title", optionMap.get("title")); // List<CodeVO>		
				
		return "insa/edc";

	}
	
	
	//법정교육조회 사용자페이지 출력
	@GetMapping("/edcUser")
	public String edcUserRender(Model model) {		
		
		model.addAttribute("test", "testone");
		return "insa/edcUser";

	}
	
	//다중입력조회
	@GetMapping("/edcJohoe")
	@ResponseBody
	public List<EdcVO> edcJohoe(EdcVO johoeKeyword) {
		
		System.out.println("모나오니 다중입력조회"+johoeKeyword);
		/*
		 * EdcVO johoeKeyword = new EdcVO(); 
		 * johoeKeyword.setEdcNm(edcNm);
		 * johoeKeyword.setEdcTy(edcTy); 
		 * johoeKeyword.setEdcBeginDt(edcBeginDt);
		 */
			
		
		return edcService.getListEdcJohoe(johoeKeyword);
	}
	
	//교육 대상자들 조회
		@GetMapping("/edcDetaJohoe")
		@ResponseBody
		public List<EdcVO> edcDetaJohoe(EdcVO keyword) {
			
			System.out.println("모나오니" + keyword);				
			
			return edcService.getListEdcDetaJohoe(keyword);
		}
		
	//교육 대상자 등록
		@PostMapping("/edcIdRegist")
		@ResponseBody
		public List<EdcVO> empRegistAdd(@RequestBody EdcVO keyword) throws ParseException {
			System.out.println("모나오니" + keyword);	
			edcService.setDbEdcAdd(keyword);
			
			EdcVO johoeKeyword = new EdcVO();
			johoeKeyword.setEdcNm("");
			johoeKeyword.setEdcTy("법정의무교육");
			String s = "2025-01-01";
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(s);
			johoeKeyword.setEdcBeginDt(d);   // ✅ Date 타입 그대로 전달   
					return edcService.getListEdcJohoe(johoeKeyword);
		}
		
	//교육 대상자들 조회
			@GetMapping("/edcUserJohoe")
			@ResponseBody
			public List<EdcVO> edcUserJohoe(EdcVO keyword) {							
				
				return edcService.getListEdcUserJohoe(keyword);
			}
}
