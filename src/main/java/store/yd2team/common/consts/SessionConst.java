package store.yd2team.common.consts;

public final class SessionConst {
	private SessionConst() {}

    public static final String LOGIN_EMP = "LOGIN_EMP";
    public static final String LOGIN_CAPTCHA_ANSWER = "LOGIN_CAPTCHA_ANSWER";
    
    public static final String LOGIN_OTP_CODE = "LOGIN_OTP_CODE";           // OTP 코드
    public static final String LOGIN_OTP_EXPIRE = "LOGIN_OTP_EXPIRE";       // 만료 시각
    public static final String PENDING_LOGIN_EMP = "PENDING_LOGIN_EMP";     // OTP 대기 중 계정 정보
    public static final String LOGIN_OTP_FAIL_CNT    = "LOGIN_OTP_FAIL_CNT";    // 현재 OTP 실패 횟수
    public static final String LOGIN_OTP_FAIL_LIMIT  = "LOGIN_OTP_FAIL_LIMIT";  // 허용 실패 횟수(정책)
    
    public static final String SESSION_TIMEOUT_MIN    = "SESSION_TIMEOUT_MIN";
    public static final String SESSION_TIMEOUT_ACTION = "SESSION_TIMEOUT_ACTION";
}
