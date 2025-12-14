// src/main/java/store/yd2team/insa/service/CalGrpVO.java
package store.yd2team.insa.service;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 급여계산그룹 VO (tb_pay_cal_grp)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalGrpVO {
    private Long grpNo;       // 그룹번호
    private String grpNm;     // 그룹명

    private Date creaDt;
    private String creaBy;
    private Date updtDt;
    private String updtBy;

    private String vendId;    // 거래처ID
}
