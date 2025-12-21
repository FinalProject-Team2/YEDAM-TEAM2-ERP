// src/main/java/store/yd2team/insa/service/SalySpecItemVO.java
package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * tb_saly_spec_item (급여명세서 항목)
 * ※ 지웅님 요구사항:
 *  - grp_id가 아니라 grp_no (급여계산그룹번호 FK)
 *  - 표시번호(disp_no) 기준 정렬을 위해 item_no(항목번호) 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalySpecItemVO {

	private Long   itemNo;         // PK SalySpecItemVO PK
    private String salySpecId;     // FK tb_saly_spec
    private String   grpNm;          // 급여계산그룹의 그룹명
    private String itemNm;         // 수당공제명 표시하는거
    private Long dispNo; // 표시번호 저장용 
    private Long   amt;            // 금액
    private String itemTy;         // 'A' 수당 / 'D' 공제

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;
    private String creaBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;
    private String updtBy;
}
