package store.yd2team.common.service;

import lombok.Data;

@Data
public class RoleVO {
	
	private String roleId;   // ROLE_001 ...
    private String vendId;   // 회사 ID (NULL 허용이면 String만 두면 됨)
    private String roleNm;   // 역할 명
    private String roleTy;   // 역할 유형 (d1=인사, d2=공통, d3=영업 같은 코드)
    private String roleDc;   // 역할 설명
    private String yn;       // Y/N

    private String creaBy;
    private String updtBy;

}
