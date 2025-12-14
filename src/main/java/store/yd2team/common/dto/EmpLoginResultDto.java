package store.yd2team.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.OprtrAcctVO;

@Data
@NoArgsConstructor
public class EmpLoginResultDto {

    private boolean success;
    private String message;
    private EmpAcctVO empAcct;
    private OprtrAcctVO oprtrAcct;

    private boolean captchaRequired;
    private boolean otpRequired;
    private boolean subscriptionRequired;

    // ✅ A 방식용: 프론트가 success=true면 여기로 이동
    private String redirectUrl;

    // ✅ 필드 전부 받는 생성자(직접 정의) - @AllArgsConstructor 쓰지 말고 이걸로 고정
    public EmpLoginResultDto(boolean success, String message, EmpAcctVO empAcct,
                             boolean captchaRequired, boolean otpRequired,
                             boolean subscriptionRequired, String redirectUrl) {
        this.success = success;
        this.message = message;
        this.empAcct = empAcct;
        this.captchaRequired = captchaRequired;
        this.otpRequired = otpRequired;
        this.subscriptionRequired = subscriptionRequired;
        this.redirectUrl = redirectUrl;
    }

    public static EmpLoginResultDto ok(EmpAcctVO vo) {
        return new EmpLoginResultDto(true, null, vo, false, false, false, null);
    }

    public static EmpLoginResultDto ok() {
        return new EmpLoginResultDto(true, null, null, false, false, false, null);
    }

    public static EmpLoginResultDto fail(String message) {
        return new EmpLoginResultDto(false, message, null, false, false, false, null);
    }

    public static EmpLoginResultDto fail(String message, boolean captchaRequired, boolean otpRequired) {
        return new EmpLoginResultDto(false, message, null, captchaRequired, otpRequired, false, null);
    }

    public static EmpLoginResultDto captchaFail(String message) {
        return new EmpLoginResultDto(false, message, null, true, false, false, null);
    }

    public static EmpLoginResultDto otpStep(EmpAcctVO vo, String message) {
        return new EmpLoginResultDto(false, message, vo, false, true, false, null);
    }

    // ✅ 서비스에서 “구독 필요”를 표시할 때 사용 (여기서는 success=false 유지)
    public static EmpLoginResultDto subscriptionRequired(EmpAcctVO vo, String message) {
        return new EmpLoginResultDto(false, message, vo, false, false, true, null);
    }
}
