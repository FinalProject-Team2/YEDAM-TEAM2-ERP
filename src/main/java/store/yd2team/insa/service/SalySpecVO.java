// src/main/java/store/yd2team/insa/service/SalySpecVO.java
package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * tb_saly_spec (급여명세서)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalySpecVO {

    private String salySpecId;   // 급여명세서 ID (PK)
    private String salyLedgId;   // 급여대장 ID FK
    private String empId;        // 사원 ID FK

    private Long payAmt;         // 지급액 합
    private Long ttDucAmt;       // 공제합
    private Long actPayAmt;      // 실지급액

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;
    private String creaBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;
    private String updtBy;
}
