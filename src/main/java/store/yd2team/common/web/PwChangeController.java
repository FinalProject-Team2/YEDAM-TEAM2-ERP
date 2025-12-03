package store.yd2team.common.web;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpPasswordForm;
import store.yd2team.common.dto.PwChangeResultDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.EmpAcctService;

@RestController
@RequiredArgsConstructor
public class PwChangeController {

    private final EmpAcctService empAcctService;

    // 비밀번호 변경 화면 진입용 - 초기 값 조회 (vendId, loginId 채워서 내려줌)
    @GetMapping("/mypage/pwChange")
    public EmpPasswordForm pwChangeForm(HttpSession session) {
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);

        EmpPasswordForm form = new EmpPasswordForm();
        if (loginEmp != null) {
            form.setVendId(loginEmp.getVendId());
            form.setLoginId(loginEmp.getLoginId());
        }

        // 프론트에서 이 JSON 받아서 화면에 뿌리면 됨
        return form;
    }

    // 비밀번호 변경 처리 (JSON/폼 → JSON 결과)
    @PostMapping("/mypage/pwChange")
    public PwChangeResultDto pwChange(
            HttpSession session,
            @Valid @ModelAttribute("form") EmpPasswordForm form,
            BindingResult bindingResult) {

        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        if (loginEmp == null) {
            return PwChangeResultDto.fail("로그인이 필요합니다.");
        }

        // 세션 기준으로 확정
        form.setVendId(loginEmp.getVendId());
        form.setLoginId(loginEmp.getLoginId());

        // 1) 정책 기반 @Valid 실패 시 → 메시지 리턴
        if (bindingResult.hasErrors()) {
            // 여러 에러 중 첫 번째 메시지만 내려주는 버전
            String msg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return PwChangeResultDto.fail(msg);
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

        return PwChangeResultDto.ok("비밀번호가 정상적으로 변경되었습니다.");
    }
}
