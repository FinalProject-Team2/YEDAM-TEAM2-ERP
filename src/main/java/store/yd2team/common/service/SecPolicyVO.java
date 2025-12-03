package store.yd2team.common.service;

import java.util.Date;

import lombok.Data;
import static store.yd2team.common.consts.CodeConst.Yn.*;

@Data
public class SecPolicyVO {
	

    private String policyId; // 정책 ID
    private String vendId;   // 회사 코드

    private Integer pwFailCnt;     // 허용 실패 횟수
    private Integer autoUnlockTm;  // 자동 잠금 해제 시간(분)

    private Integer pwLenMin;  // 비밀번호 최소 길이
    private Integer pwLenMax;  // 비밀번호 최대 길이

    // ===== 비밀번호 구성 조건 (e1/e2: Y/N) =====
    private String useUpperYn;  // 대문자 사용 여부
    private String useLowerYn;  // 소문자 사용 여부
    private String useNumYn;    // 숫자 사용 여부
    private String useSpclYn;   // 특수문자 사용 여부

    // ===== 캡챠 정책 (e1/e2: Y/N) =====
    private String captchaYn;       // 캡차 사용 여부
    private Integer captchaFailCnt; // 비밀번호 n회 틀릴 때 캡챠 활성화

    // ===== OTP 정책 (e1/e2: Y/N) =====
    private String otpYn;        // OTP 사용 여부
    private Integer otpValidMin; // OTP 유효 시간(분)
    private Integer otpFailCnt;  // OTP 실패 허용 횟수

    // ===== session 정책 (t1/t2: AUTO/WARN) =====
    private Integer sessionTimeoutMin; // 세션 타임 아웃(분)
    private String timeoutAction;      // 타임 아웃 동작 (t1/t2)

    private Date creaDt;
    private String creaBy;
    private Date updtDt;
    private String updtBy;

    // ==========================
    // Y/N(e그룹) 헬퍼 메서드
    // ==========================

    public boolean isUseUpper() { return Y.equals(this.useUpperYn); }
    public boolean isUseLower() { return Y.equals(this.useLowerYn); }
    public boolean isUseNum()   { return Y.equals(this.useNumYn); }
    public boolean isUseSpcl()  { return Y.equals(this.useSpclYn); }

    public boolean isCaptchaOn() { return Y.equals(this.captchaYn); }

    public boolean hasCaptchaThreshold() {
        return isCaptchaOn() && captchaFailCnt != null && captchaFailCnt > 0;
    }

    public boolean isOtpOn() { return Y.equals(this.otpYn); }

    public boolean hasOtpThreshold() {
        return isOtpOn()
                && otpValidMin != null && otpValidMin > 0
                && otpFailCnt  != null && otpFailCnt  > 0;
    }

    // ==========================
    // 기본값 팩토리 메서드
    // ==========================
    public static SecPolicyVO defaultPolicy() {
        SecPolicyVO vo = new SecPolicyVO();

        vo.setPwFailCnt(5);
        vo.setAutoUnlockTm(30);
        vo.setPwLenMin(8);
        vo.setPwLenMax(20);

        vo.setUseUpperYn(Y); // e1
        vo.setUseLowerYn(Y);
        vo.setUseNumYn(Y);
        vo.setUseSpclYn(Y);

        vo.setCaptchaYn(N);      // e2
        vo.setCaptchaFailCnt(3);

        vo.setOtpYn(N);
        vo.setOtpValidMin(5);
        vo.setOtpFailCnt(5);

        vo.setSessionTimeoutMin(30);
        vo.setTimeoutAction("t2"); // 경고 후 로그아웃

        return vo;
    }
    
}
