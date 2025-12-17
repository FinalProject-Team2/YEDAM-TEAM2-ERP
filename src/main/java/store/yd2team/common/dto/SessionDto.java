package store.yd2team.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto implements Serializable{
	// Serializable: “직렬화 가능하다”는 표시만 해주는 인터페이스 
	
	private String empAcctId; // tb_emp_acct PK
    private String vendId;    // 회사 코드
    private String empId;     // 사원 ID
    private String loginId;	  // 로그인한 ID
    private String empNm;     // 사원명
    private String deptId;    // 부서 ID
    private String deptNm;    // 부서명
    private String masYn;     // 마스터 여부
    private String addr;      // 거래처 주소
    private String bizcnd;    // 거래처 업종
    private String cttpc;	  // 사원 연락처
    private String hp;        // 거래처 핸드폰 번호
    private String tempYn;    // 임시 비밀번호 여부
    private String email;		// 이메일
    private String proofPhoto; // 사진
    private String oprtrYn;
    private String oprtrAcctId;
    private String jobNm;
 
    private List<String> roleIds;
    private Set<String> authCodes;
    private String roleId;
    private String acctSt;
    
    private Map<String, MenuAuthDto> menuAuthMap = new HashMap<>();
    private Map<String, MenuAuthDto> menuAuthByUrl = new HashMap<>();

}
