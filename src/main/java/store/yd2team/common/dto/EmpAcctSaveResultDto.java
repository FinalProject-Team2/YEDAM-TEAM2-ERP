package store.yd2team.common.dto;

import lombok.Data;

@Data
public class EmpAcctSaveResultDto {
	private boolean success;
    private boolean smsSent;
    private String acctStatus;
    private String empAcctId;
    
    private String errorCode;   
    private String errorMessage;
}
