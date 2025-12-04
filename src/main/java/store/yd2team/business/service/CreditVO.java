package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CreditVO {
  
    private Long cdtlnNo;
    private String custcomId;
    private String custcomName;
    private String creditGrad;
    private String cdtlnCheck;
    private Long mrtggLmt;
    private Long creditLmt;
    private Long cdtlnLmt;
    private String lmtoverCheck;
    private String yn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate applcBeginDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate applcEndDt;
    private LocalDate creaDt;
	/* private String creaBy; */
    
    
    // 고객사 상세 (tb_custcom 컬럼)
    private String bizAddr;
    private String bizTel;
    private String bizFax;
    private String psch;
    private String pschTel;
    private String pschEmail;
}
