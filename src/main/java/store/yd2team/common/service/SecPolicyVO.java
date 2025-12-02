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

    private String pwLenMin;  // 비밀번호 최소 길이
    private String pwLenMax;  // 비밀번호 최대 길이

    // ===== 비밀번호 구성 조건 (e1/e2: Y/N) =====
    private String useUpperYn;  // 대문자 사용 여부
    private String useLowerYn;  // 소문자 사용 여부
    private String useNumYn;    // 숫자 사용 여부
    private String useSpclYn;   // 특수문자 사용 여부

    // ===== 캡챠 정책 (e1/e2: Y/N) =====
    private String captchaYn;      // 캡차 사용 여부
    private Integer captchaFailCnt; // 비밀번호 n회 틀릴 때 캡챠 활성화
    
 // ===== otp 정책 (e1/e2: Y/N) =====
    private String otpYn;        // OTP 사용 여부 (e1/e2)
    private Integer otpValidMin; // OTP 유효 시간(분)
    private Integer otpFailCnt;  // OTP 실패 허용 횟수 (선택)

    private Date creaDt;
    private String creaBy;
    private Date updtDt;
    private String updtBy;

    // ==========================
    // Y/N(e그룹) 헬퍼 메서드
    // ==========================

    /** 비밀번호에 대문자 요구 여부 */
    public boolean isUseUpper() {
        return Y.equals(this.useUpperYn);
    }

    /** 비밀번호에 소문자 요구 여부 */
    public boolean isUseLower() {
        return Y.equals(this.useLowerYn);
    }

    /** 비밀번호에 숫자 요구 여부 */
    public boolean isUseNum() {
        return Y.equals(this.useNumYn);
    }

    /** 비밀번호에 특수문자 요구 여부 */
    public boolean isUseSpcl() {
        return Y.equals(this.useSpclYn);
    }

    /** 캡챠 기능이 켜져 있는지 여부 */
    public boolean isCaptchaOn() {
        return Y.equals(this.captchaYn);
    }

    /** 캡챠를 쓰긴 쓰는데, 활성화 기준 실패 횟수가 설정돼 있는지 */
    public boolean hasCaptchaThreshold() {
        return isCaptchaOn() && captchaFailCnt != null && captchaFailCnt > 0;
    }

}
