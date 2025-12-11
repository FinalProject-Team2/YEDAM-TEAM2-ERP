package store.yd2team.common.dto;

import java.util.Date;
import lombok.Data;

@Data
public class LockAccountDto {

    private String empAcctId;   // 계정 PK
    private String vendId;      // 회사 코드
    private String clientName;  // 거래처 명 (tb_vend.vend_nm)
    private String accountId;   // 계정 ID (login_id)
    private String userName;    // 사용자 이름 (tb_emp.nm)
    private String status;      // 상태 코드 ID (r1, r2, r3, r4)
    private String statusNm;    // 상태 코드 명 (ACTIVE/LOCKED...)
    private Date   lockTime;    // 잠금 일시 (tb_emp_acct.lock_dttm)
}
