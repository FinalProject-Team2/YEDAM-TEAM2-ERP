// src/main/java/store/yd2team/insa/service/SalyLedgVO.java
package store.yd2team.insa.service;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * tb_saly_ledg (급여대장)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalyLedgVO {

    private String salyLedgId;   // 급여대장 ID (PK)
    private String salyLedgNm;   // 급여대장 명칭

    // 화면: yyyy-MM
    private String revsYm;

    // 급여대장 상태 코드 (sal1 / sal2 / sal3)
    private String salyLedgSt;

    // 급여대장 상태명 (tb_code.code_nm: 미확정 / 확정 / 지급완료)
    private String salyLedgStNm;

    // 화면: yyyy-MM-dd
    private String payDt;

    private Integer rcnt;        // 인원수
    private Double ttPayAmt;     // 총지급액

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;
    private String creaBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;
    private String updtBy;

    private String vendId;

    // 모달에서 넘어오는 선택 사원 목록
    private List<String> empIdList;
}
