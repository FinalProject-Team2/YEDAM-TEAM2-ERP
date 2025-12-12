package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.yd2team.common.service.EmpAcctVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpLoginResultDto {
	
	private boolean success;
    private String message;
    private EmpAcctVO empAcct;

    private boolean captchaRequired;
    private boolean otpRequired; 

    public static EmpLoginResultDto ok(EmpAcctVO vo) {
        return new EmpLoginResultDto(true, null, vo, false, false);
    }

    public static EmpLoginResultDto ok() {
        return new EmpLoginResultDto(true, null, null, false, false);
    }

    // 기본 실패 (캡챠/OTP 둘다 필요 없음)
    public static EmpLoginResultDto fail(String message) {
        return fail(message, false, false);
    }

    // 실패 + 캡챠/OTP 플래그까지 함께 설정
    public static EmpLoginResultDto fail(String message,
                                      boolean captchaRequired,
                                      boolean otpRequired) {
        return new EmpLoginResultDto(false, message, null, captchaRequired, otpRequired);
    }

    // 캡챠 검증 실패
    public static EmpLoginResultDto captchaFail(String message) {
        return fail(message, true, false);
    }

    // ID/PW는 맞았지만 OTP가 필요한 상태
    public static EmpLoginResultDto otpStep(EmpAcctVO vo, String message) {
        return new EmpLoginResultDto(false, message, vo, false, true);
    }
    
    
}
