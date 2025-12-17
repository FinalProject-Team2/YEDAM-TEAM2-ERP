package store.yd2team.common.dto;

import lombok.Data;

@Data
public class RecentAuthChangeDto {
	private String motionTyName;
	private String loginId;
    private String errDttm;
    private String empAcctId;
    private String vendId;
    private String motionTy; // ro1, au1
    private String smry;
    private String creaBy;
}
