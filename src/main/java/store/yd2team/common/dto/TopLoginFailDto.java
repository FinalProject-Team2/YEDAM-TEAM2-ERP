package store.yd2team.common.dto;

import lombok.Data;

@Data
public class TopLoginFailDto {
	private String loginId;
    private String empAcctId;
    private int failCnt;
    private String lastFailDttm;
}
