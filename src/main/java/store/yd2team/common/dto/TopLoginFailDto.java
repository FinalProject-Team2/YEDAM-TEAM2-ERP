package store.yd2team.common.dto;

import java.util.Date;
import lombok.Data;

@Data
public class TopLoginFailDto {
    private String empAcctId;
    private int failCnt;
    private Date lastFailDttm;
}
