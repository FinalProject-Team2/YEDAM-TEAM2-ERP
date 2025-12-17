package store.yd2team.common.dto;

import lombok.Data;

@Data
public class EmpAcctEmployeeDto {

    // 그리드 표시용
    private String deptNm;        // 부서 명
    private String clsf;        // 직급 코드 (k1, k2…)
    private String jobNm;        // 직급 명  (사원, 대리…)

    private String empId;          // 사번
    private String nm;        // 사원 명

    // 연락처/이메일
    private String cttpc;
    private String email;

    // 계정 관련
    private String empAcctId;
    private String loginId;
    private String acctStatus;      // r1, r2, r3
    private String acctStatusName;  // ACTIVE, LOCKED, INACTIVE
    private Integer roleCnt;
    
    // 공통
    private String vendId;
}
