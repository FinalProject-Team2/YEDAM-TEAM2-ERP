package store.yd2team.common.validation;

import static store.yd2team.common.consts.CodeConst.Yn.Y;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import store.yd2team.common.dto.EmpPasswordForm;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;

/**
 * 거래처별 보안 정책(tb_sec_policy)에 따라
 * 비밀번호 길이/구성을 검증하는 Validator.
 */
@Component
public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, EmpPasswordForm> {

    @Autowired
    private SecPolicyService secPolicyService;

    // 정책이 없을 때 기본값
    private static final int DEFAULT_MIN_LEN = 8;
    private static final int DEFAULT_MAX_LEN = 20;

    @Override
    public boolean isValid(EmpPasswordForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true; // 다른 @NotNull 에 맡김
        }

        String vendId   = form.getVendId();
        String password = form.getNewPassword();

        if (password == null || password.isBlank()) {
            // @NotBlank 에서 잡도록 여기서는 true
            return true;
        }

        // 1) 보안 정책 조회 (vendId 없으면 default 정책 사용하도록 서비스에서 처리)
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

            // tb_sec_policy 컬럼은 e1/e2 → CodeConst.Yn.Y(e1) 비교
            requireUpper = Y.equals(policy.getUseUpperYn());
            requireLower = Y.equals(policy.getUseLowerYn());
            requireNum   = Y.equals(policy.getUseNumYn());
            requireSpcl  = Y.equals(policy.getUseSpclYn());
        }

        boolean valid = true;
        StringBuilder msg = new StringBuilder();

        // 2) 길이 체크
        int len = password.length();
        if (len < minLen || len > maxLen) {
            valid = false;
            appendMsg(msg, "비밀번호는 " + minLen + "자리 이상, " + maxLen + "자리 이하이어야 합니다.");
        }

        // 3) 조합 체크 (필요한 항목만)
        if (requireUpper && !password.matches(".*[A-Z].*")) {
            valid = false;
            appendMsg(msg, "영문 대문자를 최소 1자 이상 포함해야 합니다.");
        }
        if (requireLower && !password.matches(".*[a-z].*")) {
            valid = false;
            appendMsg(msg, "영문 소문자를 최소 1자 이상 포함해야 합니다.");
        }
        if (requireNum && !password.matches(".*[0-9].*")) {
            valid = false;
            appendMsg(msg, "숫자를 최소 1자 이상 포함해야 합니다.");
        }
        if (requireSpcl && !password.matches(".*[^0-9A-Za-z].*")) {
            valid = false;
            appendMsg(msg, "특수문자를 최소 1자 이상 포함해야 합니다.");
        }

        // 4) 새 비밀번호 확인 일치 여부
        String confirm = form.getNewPasswordConfirm();
        if (confirm != null && !confirm.isBlank() && !password.equals(confirm)) {
            valid = false;
            appendMsg(msg, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.toString())
                   // 클래스 레벨이지만 newPassword 필드에 에러 표시
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