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
	public String register(SignUpRequestDTO dto, Model model) throws Exception {
		// 실제 회원가입 처리 로직 호출 (tb_vend, tb_vend_acct 저장)
		String vendId = signUpService.registerVendor(dto);
		// TODO: vendId가 null 이거나 예외가 발생하면 에러 페이지로 보내는 로직을 추후 보완 가능
		
		// 회원가입 성공 시 로그인 페이지로 이동
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