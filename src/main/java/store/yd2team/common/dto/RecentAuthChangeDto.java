package store.yd2team.common.dto;

import java.util.Date;
import lombok.Data;

@Data
public class RecentAuthChangeDto {
    private Date errDttm;
    private String empAcctId;
    private String vendId;
    private String motionTy; // ro1, au1
    private String smry;
    private String creaBy;
}
