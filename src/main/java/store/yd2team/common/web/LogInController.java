package store.yd2team.common.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.LoginResultDto;
import store.yd2team.common.service.EmpAcctService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class LogInController {
	
	final EmpAcctService empAcctService;
	
	// 로그인
	@PostMapping("/logIn/login")
	public LoginResultDto login(@RequestParam("vendId") String vendId,
	                            @RequestParam("loginId") String loginId,
	                            @RequestParam("password") String password,
	                            HttpSession session) {

	    LoginResultDto result = empAcctService.login(vendId, loginId, password);

	    // 로그인 성공한 경우에만 세션 세팅
	    if (result.isSuccess() && result.getEmpAcct() != null) {

	        var empAcct = result.getEmpAcct(); // 타입: EmpAcctVO 또는 비슷한 것

	        SessionDto loginEmp = new SessionDto();
	        loginEmp.setEmpAcctId(empAcct.getEmpAcctId());
	        loginEmp.setVendId(empAcct.getVendId());
	        loginEmp.setEmpId(empAcct.getEmpId());
	        loginEmp.setLoginId(empAcct.getLoginId());
	        loginEmp.setEmpNm(empAcct.getEmpNm());
	        loginEmp.setDeptId(empAcct.getDeptId());
	        loginEmp.setDeptNm(empAcct.getDeptNm());

	        // 세션에 "한 덩어리"로 넣기
	        session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);
	        
	        log.info(">>> 세션 저장 완료: sessionId={}, empAcctId={}, empNm={}, deptNm={}, deptId={}, empId={}, loginId={}, vendId={}",
	                session.getId(),
	                loginEmp.getEmpAcctId(),
	                loginEmp.getEmpNm(),
	                loginEmp.getDeptNm(),
	                loginEmp.getDeptId(),
	                loginEmp.getEmpId(),
	                loginEmp.getLoginId(),
	                loginEmp.getVendId());
	    }

	    return result;
	}
    
    // 로그아웃
    @PostMapping("/logIn/logout")
    public LoginResultDto logout(HttpSession session) {
        session.invalidate();
        return LoginResultDto.ok(null); 
    }
	
}
