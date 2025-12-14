// store/yd2team/insa/service/CalGrpItemVO.java
package store.yd2team.insa.service;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalGrpItemVO {
    private String vendId;
    private Long grpNo;
    private String itemTy;   // 'A' or 'D'
    private String itemId;   // allow_id or duc_id
    private Date creaDt;
    private String creaBy;
}
