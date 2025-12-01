package store.yd2team.common.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SessionDto implements Serializable{
	// Serializable: “직렬화 가능하다”는 표시만 해주는 인터페이스 
	
	private String empAcctId; // tb_emp_acct PK
    private String vendId;    // 회사 코드
    private String empId;     // 사원 ID
    private String loginId;	  // 로그인한 ID
    private String empNm;     // 사원명
    private String deptId;    // 부서 ID
    private String deptNm;    // 부서명
}
