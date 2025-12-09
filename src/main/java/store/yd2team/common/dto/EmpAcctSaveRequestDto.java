package store.yd2team.common.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmpAcctSaveRequestDto {
	
    private String empAcctId;   // null이면 신규
    private String vendId;
    private String empId;
    private String loginId;
    private String acctStatus;
    private List<String> roleIds;

}
