package store.yd2team.common.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import store.yd2team.common.service.SignUpService;

@Controller
public class SignUpController {
	
	@Autowired
	SignUpService signUpService;
	
	// 회원가입 페이지 이동
	@GetMapping("/SignUp")
	public String SignUp() {
		return "signUp/signUp";
	}
	
}// end class