// store/yd2team/insa/service/AllowDucVO.java
package store.yd2team.insa.service;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllowDucVO {

    // 공통
    private String vendId;      // 회사 ID
    private Integer dispNo;     // 표시번호
    private String calFmlt;     // 계산식
    private String calMthd;     // 산출방법
    private String ynCode;      // 사용여부 코드 (e1:사용, e2:미사용)

    // 수당 전용
    private String allowId;
    private String allowNm;

    // 공제 전용
    private String ducId;
    private String ducNm;

    // 공통 이력
    private Date creaDt;
    private String creaBy;
    private Date updtDt;
    private String updtBy;
}
