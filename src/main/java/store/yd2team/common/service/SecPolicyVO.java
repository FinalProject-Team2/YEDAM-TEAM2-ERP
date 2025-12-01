package store.yd2team.common.service;

import java.util.Date;

import lombok.Data;

@Data
public class SecPolicyVO {
	
	private String policyId;
    private String vendId;

    private Integer pwFailCnt;     // 허용 실패 횟수
    private Integer autoUnlockTm;  // 자동 잠금 해제 시간(분)

    private String pwLenMin;
    private String pwLenMax;
    private String useUpperYn;
    private String useLowerYn;
    private String useNumYn;
    private String useSpclYn;

    private Date creaDt;
    private String creaBy;
    private Date updtDt;
    private String updtBy;

}
