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
	public LoginResultDto login(@RequestParam("vendId") String vendId, @RequestParam("loginId") String loginId,
			@RequestParam("password") String password,
			@RequestParam(value = "captchaValue", required = false) String captchaValue, HttpSession session) {

		// ==========================
		// 1) 캡챠 검증 (컨트롤러 레이어)
		// ==========================

		// 세션에 저장된 정답
		String answer = (String) session.getAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER);

		// 일단은 "항상 캡챠 사용하는 모드" 기준으로 작성
		if (answer == null) {
		    return LoginResultDto.captchaFail("보안문자를 다시 받아주세요.");
		}

		if (captchaValue == null || captchaValue.isBlank()
		        || !answer.equalsIgnoreCase(captchaValue.trim())) {
		    return LoginResultDto.captchaFail("보안문자를 정확히 입력해 주세요.");
		}

		// 캡챠 통과했으면 한 번 쓰고 제거 (재사용 방지)
		session.removeAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER);

		// ==========================
		// 2) 실제 로그인 서비스 호출
		// ==========================

		LoginResultDto result = empAcctService.login(vendId, loginId, password);

		// 로그인 성공한 경우에만 세션 세팅
		if (result.isSuccess() && result.getEmpAcct() != null) {

			var empAcct = result.getEmpAcct(); // EmpAcctVO 같은 타입

			SessionDto loginEmp = new SessionDto();
			loginEmp.setEmpAcctId(empAcct.getEmpAcctId());
			loginEmp.setVendId(empAcct.getVendId());
			loginEmp.setEmpId(empAcct.getEmpId());
			loginEmp.setLoginId(empAcct.getLoginId());
			loginEmp.setEmpNm(empAcct.getEmpNm());
			loginEmp.setDeptId(empAcct.getDeptId());
			loginEmp.setDeptNm(empAcct.getDeptNm());

			session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);

			log.info(
					">>> 로그인 + 세션 저장 완료: sessionId={}, empAcctId={}, empNm={}, deptNm={}, deptId={}, empId={}, loginId={}, vendId={}",
					session.getId(), loginEmp.getEmpAcctId(), loginEmp.getEmpNm(), loginEmp.getDeptNm(),
					loginEmp.getDeptId(), loginEmp.getEmpId(), loginEmp.getLoginId(), loginEmp.getVendId());
		}

		return result;
	}

	// 로그아웃
	@PostMapping("/logIn/logout")
	public LoginResultDto logout(HttpSession session) {
		session.invalidate();
		return LoginResultDto.ok();
	}

}
