// src/main/java/store/yd2team/insa/service/PayItemRowVO.java
package store.yd2team.insa.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * prc_calc_saly_items 의 결과 커서( pay_item_row ) 1건 매핑용
 * itemTy : 'A' 수당 / 'D' 공제
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayItemRowVO {
    private String itemTy;   // A/D
    private String itemId;   // allow_id or duc_id
    private String itemNm;   // allow_nm or duc_nm
    private Long dispNo;
    private Long   amt;      // 계산 금액
}
