package store.yd2team.common.web;

import static store.yd2team.common.consts.CodeConst.Yn.Y;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpPasswordForm;
import store.yd2team.common.dto.PwChangeResultDto;
import store.yd2team.common.dto.PwPolicyInfoDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.EmpAcctService;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;

@RestController
@RequiredArgsConstructor
public class PwChangeController {

    private final EmpAcctService empAcctService;
    private final SecPolicyService secPolicyService;

    // (필요하면) 초기값 조회용 GET
    @GetMapping("/mypage/pwChange")
    public EmpPasswordForm pwChangeForm(HttpSession session) {
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);

        EmpPasswordForm form = new EmpPasswordForm();
        if (loginEmp != null) {
            form.setVendId(loginEmp.getVendId());
            form.setLoginId(loginEmp.getLoginId());
        }
        return form;
    }

    // 비밀번호 변경 처리 (JSON → JSON 결과)
    @PostMapping("/mypage/pwChange")
    public PwChangeResultDto pwChange(
            HttpSession session,
            @Valid @RequestBody EmpPasswordForm form,
            BindingResult bindingResult) {

        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        if (loginEmp == null) {
            return PwChangeResultDto.fail("로그인이 필요합니다.");
        }

        // 세션 기준으로 확정 (클라이언트가 조작 못하게)
        form.setVendId(loginEmp.getVendId());
        form.setLoginId(loginEmp.getLoginId());

        // 1) @Valid 실패 시
        if (bindingResult.hasErrors()) {
        	
        	String msg;

            // 1-1) 비밀번호 관련 에러면 newPassword 쪽 메시지 우선 사용
            if (bindingResult.hasFieldErrors("newPassword")
                    || bindingResult.hasFieldErrors("newPasswordConfirm")) {

                if (bindingResult.getFieldError("newPassword") != null) {
                    msg = bindingResult.getFieldError("newPassword").getDefaultMessage();
                } else {
                    msg = bindingResult.getFieldError("newPasswordConfirm").getDefaultMessage();
                }

            } else {
                // 1-2) 그 외(현재 비밀번호 미입력 등)는 첫 메시지 사용
                msg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            }

            return PwChangeResultDto.fail(msg);
        	
        	
			/* 나중에 활성화 하면 비밀번호 규칙을 확인해주세요. 문구만 출력
			 * // 새 비밀번호/확인 비밀번호 쪽 에러면 공통 문구로 if
			 * (bindingResult.hasFieldErrors("newPassword") ||
			 * bindingResult.hasFieldErrors("newPasswordConfirm")) { return
			 * PwChangeResultDto.fail("비밀번호 규칙을 확인해주세요."); }
			 * 
			 * // 그 외(현재 비밀번호 미입력 등)는 기존 메시지 사용 String msg =
			 * bindingResult.getAllErrors().get(0).getDefaultMessage(); return
			 * PwChangeResultDto.fail(msg);
			 */
        }

        // 2) 현재 비밀번호 확인
        boolean currentOk = empAcctService.checkPassword(
                form.getVendId(),
                form.getLoginId(),
                form.getCurrentPassword()
        );

        if (!currentOk) {
            return PwChangeResultDto.fail("현재 비밀번호가 올바르지 않습니다.");
        }
        
        // 2-1) 현재 비밀번호와 새 비밀번호가 동일한지 검사  ✅ 추가 부분
        String currentPw = form.getCurrentPassword();
        String newPw     = form.getNewPassword();
        if (currentPw != null && newPw != null && currentPw.equals(newPw)) {
            return PwChangeResultDto.fail("현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
        }
        
        // 3) 새 비밀번호 저장
        empAcctService.changePassword(
                form.getVendId(),
                form.getLoginId(),
                form.getNewPassword()
        );

        // 4) 임시 비밀번호였다면 플래그 해제
        empAcctService.clearTempPasswordFlag(
                form.getVendId(),
                form.getLoginId()
        );
        
        // 5) 세션에도 반영: 이제 임시 비밀번호 아님
        loginEmp.setTempYn("e2"); // 위에서 이미 가져온 객체 그대로 사용
        session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);


        return PwChangeResultDto.ok("비밀번호가 정상적으로 변경되었습니다.");
    }

    /**
     * 비밀번호 정책 안내 조회
     *  - 상단 ! 안내 문구 + 길이 문구 + 조합 규칙 HTML
     */
    @GetMapping("/mypage/pwPolicyInfo")
    public PwPolicyInfoDto getPwPolicyInfo(HttpSession session) {

        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        if (loginEmp == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 거래처별 정책 조회 (없으면 default 로직은 서비스에서 처리)
        SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(loginEmp.getVendId());

        // 기본값 (정책 없을 때)
        int minLen = 8;
        int maxLen = 20;

        boolean requireUpper = false;
        boolean requireLower = false;
        boolean requireNum   = false;
        boolean requireSpcl  = false;

        if (policy != null) {
            if (policy.getPwLenMin() != null && policy.getPwLenMin() > 0) {
                minLen = policy.getPwLenMin();
            }
            if (policy.getPwLenMax() != null && policy.getPwLenMax() > 0) {
                maxLen = policy.getPwLenMax();
            }

            // Y == e1 이라고 가정 (CodeConst.Yn.Y)
            requireUpper = Y.equals(policy.getUseUpperYn());
            requireLower = Y.equals(policy.getUseLowerYn());
            requireNum   = Y.equals(policy.getUseNumYn());
            requireSpcl  = Y.equals(policy.getUseSpclYn());
        }

        // 1) 맨 위 ! 안내 문구
        String guide = "비밀번호는 아래의 비밀번호 정책을 모두 만족해야 합니다.";

        // 2) 길이 안내
        String lengthText = "비밀번호는 " + minLen + "자리 이상, " + maxLen + "자리 이하이어야 합니다.";

        // 3) 조합 규칙 (켜져 있는 정책만 줄바꿈으로 연결)
        StringBuilder sb = new StringBuilder();
        appendRule(sb, requireUpper, "영문 대문자를 최소 1자 이상 포함해야 합니다.");
        appendRule(sb, requireLower, "영문 소문자를 최소 1자 이상 포함해야 합니다.");
        appendRule(sb, requireNum,   "숫자를 최소 1자 이상 포함해야 합니다.");
        appendRule(sb, requireSpcl,  "특수문자를 최소 1자 이상 포함해야 합니다.");

        String ruleHtml = sb.toString();

        PwPolicyInfoDto dto = new PwPolicyInfoDto();
        dto.setGuide(guide);
        dto.setLengthText(lengthText);
        dto.setRuleHtml(ruleHtml);

        return dto;
    }

    private void appendRule(StringBuilder sb, boolean enabled, String text) {
        if (!enabled) return;
        if (sb.length() > 0) sb.append("<br>");
        sb.append(text);
    }
}
