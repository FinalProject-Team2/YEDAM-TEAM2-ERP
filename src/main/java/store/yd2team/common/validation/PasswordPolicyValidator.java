package store.yd2team.common.validation;

import static store.yd2team.common.consts.CodeConst.Yn.Y;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpPasswordForm;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;

/**
 * 거래처별 보안 정책(tb_sec_policy)에 따라
 * 비밀번호 길이/구성을 검증하는 Validator.
 */
@Slf4j
@Component
public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, EmpPasswordForm> {

	@Autowired
    private SecPolicyService secPolicyService;

    @Autowired(required = false)
    private HttpSession session;

    private static final int DEFAULT_MIN_LEN = 8;
    private static final int DEFAULT_MAX_LEN = 20;

    @Override
    public boolean isValid(EmpPasswordForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        // 0) vendId 결정: 세션 기준으로 우선
        String vendId = null;

        if (session != null) {
            Object obj = session.getAttribute(SessionConst.LOGIN_EMP);
            if (obj instanceof SessionDto loginEmp && loginEmp.getVendId() != null) {
                vendId = loginEmp.getVendId();
            }
        }

        // 세션이 없거나 실패하면 폼 값 fallback (혹시 다른 용도로 재사용할 수도 있으니)
        if (vendId == null || vendId.isBlank()) {
            vendId = form.getVendId();
        }

        String password = form.getNewPassword();
        if (password == null || password.isBlank()) {
            return true;
        }

        SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(
                (vendId == null || vendId.isBlank()) ? null : vendId);

        int minLen = DEFAULT_MIN_LEN;
        int maxLen = DEFAULT_MAX_LEN;

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

            requireUpper = Y.equals(policy.getUseUpperYn());
            requireLower = Y.equals(policy.getUseLowerYn());
            requireNum   = Y.equals(policy.getUseNumYn());
            requireSpcl  = Y.equals(policy.getUseSpclYn());
        }

        boolean valid = true;
        StringBuilder msg = new StringBuilder();

        appendMsg(msg, "비밀번호 규칙을 확인해 주세요.");

        int len = password.length();
        if (len < minLen || len > maxLen) {
            valid = false;
            appendMsg(msg, "<br>비밀번호는 " + minLen + "자리 이상, " + maxLen + "자리 이하이어야 합니다.");
        }

        if (requireUpper && !password.matches(".*[A-Z].*")) {
            valid = false;
            appendMsg(msg, "<br>영문 대문자를 최소 1자 이상 포함해야 합니다.");
        }
        if (requireLower && !password.matches(".*[a-z].*")) {
            valid = false;
            appendMsg(msg, "<br>영문 소문자를 최소 1자 이상 포함해야 합니다.");
        }
        if (requireNum && !password.matches(".*[0-9].*")) {
            valid = false;
            appendMsg(msg, "<br>숫자를 최소 1자 이상 포함해야 합니다.");
        }
        if (requireSpcl && !password.matches(".*[^0-9A-Za-z].*")) {
            valid = false;
            appendMsg(msg, "<br>특수문자를 최소 1자 이상 포함해야 합니다.");
        }

        String confirm = form.getNewPasswordConfirm();
        if (confirm != null && !confirm.isBlank() && !password.equals(confirm)) {
            valid = false;
            appendMsg(msg, "<br>새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.toString())
                   .addPropertyNode("newPassword")
                   .addConstraintViolation();
        }

        return valid;
    }

    private void appendMsg(StringBuilder sb, String add) {
        if (sb.length() > 0) sb.append(" ");
        sb.append(add);
    }
    
}