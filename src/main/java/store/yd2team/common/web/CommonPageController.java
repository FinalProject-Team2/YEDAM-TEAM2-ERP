package store.yd2team.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonPageController {
	
	// 공통 코드 관리
	@GetMapping("/code")
	public String commonCode() {
		return "common/commonCode";
	}
	
	// 사원 계정 관리
	@GetMapping("/empAcct")
	public String empAcct() {
		return "common/empAcct";
	}
	
	// 권한 관리
	@GetMapping("/auth")
	public String auth() {
		return "common/auth";
	}
	
	// 로그인 정책
	@GetMapping("/loginPolicy")
	public String loginPolicy() {
		return "common/loginPolicy";
	}
	
	// 시스템 로그
	@GetMapping("/sysLog")
	public String sysLog() {
		return "common/sysLog";
	}
	
	// 잠금 계정 해제
	@GetMapping("/unlockAcct")
	public String unlockAcct() {
		return "common/unlockAcct";
	}
	
}
