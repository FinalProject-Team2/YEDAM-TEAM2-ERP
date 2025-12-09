package store.yd2team.common.dto;

import lombok.Data;

@Data
public class EmpAcctRoleDto {
	
    private String roleId;    // 역할 ID
    private String roleNm;    // 역할 명
    private String roleTy;    // 역할 유형 코드 (예: r1/r2…)
    private String roleTyNm;  // 역할 유형 한글명
    private String useYn;     // e1/e2 (Y/N)

}
