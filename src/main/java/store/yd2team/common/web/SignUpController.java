package store.yd2team.common.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import store.yd2team.common.service.SignUpService;
import store.yd2team.common.dto.SignUpRequestDTO;
import store.yd2team.common.service.CodeService;
import store.yd2team.common.service.CodeVO;

@Controller
@RequestMapping("/signUp")
public class SignUpController {
	
	@Autowired
	SignUpService signUpService;
	
	@Autowired
	CodeService codeService;
	
	// 회원가입 페이지 이동
	@GetMapping
	public String SignUp(Model model) {
		// 업태/업종 코드: tb_code.grp_id = 'a'
		CodeVO codeParam = new CodeVO();
		codeParam.setGrpId("a");
		// vend_id는 공통 코드로 보고 null 처리
		codeParam.setVendId(null);
		List<CodeVO> bizTypeList = codeService.findCode(codeParam);
		model.addAttribute("bizTypeList", bizTypeList);
		
		return "signUp/signUp";
	}
	
	// 회원가입 처리
	@PostMapping
	public String register(SignUpRequestDTO dto, Model model, RedirectAttributes redirectAttributes) throws Exception {
		String vendId = signUpService.registerVendor(dto);
		// 성공 메시지 플래그를 로그인 페이지에 전달
		redirectAttributes.addAttribute("signUpSuccess", "true");
		return "redirect:/logIn";
	}
	
	// 아이디 중복 체크
	@GetMapping("/idCheck")
	@ResponseBody
	public boolean checkLoginId(@RequestParam("loginId") String loginId) {
		return signUpService.isLoginIdDuplicated(loginId);
	}
	
	// 사업자등록번호 중복 체크
	@GetMapping("/bizNoCheck")
	@ResponseBody
	public boolean checkBizNo(@RequestParam("bizNo") String bizNo) {
		return signUpService.isBizNoDuplicated(bizNo);
	}
	
}// end class