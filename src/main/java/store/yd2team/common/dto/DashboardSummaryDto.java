package store.yd2team.common.dto;

import lombok.Data;

@Data
public class DashboardSummaryDto {
    private int totalEmp;        // tb_emp 전체
    private int activeAcct;      // tb_emp_acct st='ACTIVE'
    private int noRoleAcct;      // role 미지정
    private int loginSuccToday;  // tb_log lg1
    private int loginFailToday;  // tb_log lg2
    private int otpFailToday;    // tb_log lg5
    private int lockedToday;     // tb_log lg3 (오늘 잠금발생)
    private int lockedNow;       // tb_emp_acct st='LOCKED' (현재 잠김)
    private int noAcctEmp;
}
