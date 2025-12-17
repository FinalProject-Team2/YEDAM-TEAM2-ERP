// src/main/java/store/yd2team/insa/service/SalySpecCalcViewVO.java
package store.yd2team.insa.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 급여계산 모달에서 "해당 급여대장에 포함된 사원(명세서)" 목록 출력용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalySpecCalcViewVO {
    private String salySpecId;
    private String empId;
    private String empNm;
    private String deptNm;
//    private Long grpNo;
//    private String calcGrpNm;
    private String clsfNm;    // 직급명
    private String rspofcNm;  // 직책명

    private Long grpNo;        // ✅ 다시 추가
    private String calcGrpNm;  // ✅ 다시 추가

    private Long payAmt;      // 지급합
    private Long ttDucAmt;    // 공제합
    private Long actPayAmt;   // 실지급
}
