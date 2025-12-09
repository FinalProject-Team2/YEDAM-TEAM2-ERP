package store.yd2team.common.dto;

import java.io.Serializable;

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
    private String loginId;   // 로그인한 ID
    private String empNm;     // 사원명
    private String deptId;    // 부서 ID
    private String deptNm;    // 부서명
    private String masYn;     // 마스터 여부
    private String addr;      // 거래처 주소
    private String bizcnd;    // 거래처 업종
    private String cttpc;     // 사원 연락처
    private String hp;        // 거래처 핸드폰 번호
    private String tempYn;    // 임시 비밀번호 여부

    // 🔽 추가된 필드: thymeleaf 에서 session.LOGIN_EMP.roleId 로 사용
    private String roleId;    // 역할/권한 ID (예: ROLE_HR_ADMIN, ROLE_USER)
}
