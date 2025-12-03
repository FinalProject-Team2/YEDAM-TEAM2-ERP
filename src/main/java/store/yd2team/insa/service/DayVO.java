package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 요일 (tb_day) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayVO {

    private Long dayNo;
    private Long basiNo;
    private String dayNm;   // MON/TUE/... 또는 월/화/...

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;
    private String creaBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;
    private String updtBy;
}
