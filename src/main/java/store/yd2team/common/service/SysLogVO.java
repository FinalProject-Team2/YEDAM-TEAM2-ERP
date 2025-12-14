package store.yd2team.common.service;

import lombok.Data;

@Data
public class SysLogVO {

    private String logId;
    private String empAcctId;
    private String vendId;
    private String errDttm;
    private String modlGrp;
    private String modlCode;
    private String targetTbNm;
    private String targetPk;
    private String motionTy;
    private String smry;
    private String yn;
    private String creaDt;
    private String creaBy;
    private String updtDt;
    private String updtBy;

    // 화면용 추가 필드 (JOIN 결과)
    private String loginId;  // tb_emp_acct.login_id
    
    private String modlCodeNm;   
    private String motionTyNm; 
    
    private String oprtrAcctId;
}
