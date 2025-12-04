package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HldyWkBasiVO {

    private Long basiNo;
    private String deptId;
    private String basiNm;
    private String vendId;

    @DateTimeFormat(pattern = "HH:mm")
    private Date atdcTm;

    @DateTimeFormat(pattern = "HH:mm")
    private Date afwkTm;

    // ✅ 삭제됨: hldyTy / wkDe / hldyDe

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;
    private String creaBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;
    private String updtBy;

    // -------- 조회용 --------
    private String deptNm;

    // ✅ 삭제됨: hldyTyNm
}
