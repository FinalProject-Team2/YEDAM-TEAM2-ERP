package store.yd2team.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import store.yd2team.common.validation.PasswordPolicy;

@Data
@PasswordPolicy
public class EmpPasswordForm {

    // 어떤 거래처 정책을 적용할지 (세션에서 채워 넣거나, 화면 hidden 값 등)
    private String vendId;

    // 어떤 계정인지 (현재 비번 검증용)
    private String loginId;

    // 현재 비밀번호 (비밀번호 변경 화면에서만 사용)
    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    private String currentPassword;

    // 새 비밀번호
    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    private String newPassword;

    // 새 비밀번호 확인
    @NotBlank(message = "새 비밀번호를 한 번 더 입력해 주세요.")
    private String newPasswordConfirm;
}