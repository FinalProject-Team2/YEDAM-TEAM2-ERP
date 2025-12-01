package store.yd2team.common.service;

import java.util.Date;

import lombok.Data;

@Data
public class SecPolicyVO {
	
	private String policyId; // 정책 ID
    private String vendId; // 회사 코드

    private Integer pwFailCnt;     // 허용 실패 횟수
    private Integer autoUnlockTm;  // 자동 잠금 해제 시간(분)

    private String pwLenMin; // 비밀번호 최소 길이
    private String pwLenMax; // 비밀번호 최대 길이
    private String useUpperYn; // 대문자 사용 여부
    private String useLowerYn; // 소문자 사용 여부
    private String useNumYn; // 숫자 사용 여부
    private String useSpclYn; // 특수문자 사용 여부
    private String captchaYn; // 캡차 사용 여부
    private Integer captchaFailCnt; // 비밀번호 n회 틀릴 때 캡챠 활성화
    
    private Date creaDt;
    private String creaBy;
    private Date updtDt;
    private String updtBy;

}
