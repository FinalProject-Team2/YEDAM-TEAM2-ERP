package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.yd2team.common.service.EmpAcctVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultDto {
	
    private boolean success;      // 성공 여부
    private String message;       // 에러/안내 메시지
    private EmpAcctVO empAcct;    // 로그인한 계정 정보 (성공 시)

    private boolean captchaRequired;

    public static LoginResultDto ok(EmpAcctVO vo) {
        return new LoginResultDto(true, null, vo, false);
    }

    public static LoginResultDto ok() {
        return new LoginResultDto(true, null, null, false);
    }

    public static LoginResultDto fail(String message) {
        return new LoginResultDto(false, message, null, false);
    }

    public static LoginResultDto captchaFail(String message) {
        return new LoginResultDto(false, message, null, true);
    }
}
