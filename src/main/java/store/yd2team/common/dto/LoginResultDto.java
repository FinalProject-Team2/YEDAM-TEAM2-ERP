package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.yd2team.common.service.EmpAcctVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultDto {
	
	private boolean success;
    private String message;
    private EmpAcctVO empAcct;

    private boolean captchaRequired;
    private boolean otpRequired; 

    public static LoginResultDto ok(EmpAcctVO vo) {
        return new LoginResultDto(true, null, vo, false, false);
    }

    public static LoginResultDto ok() {
        return new LoginResultDto(true, null, null, false, false);
    }

    // 기본 실패 (캡챠/OTP 둘다 필요 없음)
    public static LoginResultDto fail(String message) {
        return fail(message, false, false);
    }

    // 실패 + 캡챠/OTP 플래그까지 함께 설정
    public static LoginResultDto fail(String message,
                                      boolean captchaRequired,
                                      boolean otpRequired) {
        return new LoginResultDto(false, message, null, captchaRequired, otpRequired);
    }

    // 캡챠 검증 실패
    public static LoginResultDto captchaFail(String message) {
        return fail(message, true, false);
    }

    // ID/PW는 맞았지만 OTP가 필요한 상태
    public static LoginResultDto otpStep(EmpAcctVO vo, String message) {
        return new LoginResultDto(false, message, vo, false, true);
    }
}
