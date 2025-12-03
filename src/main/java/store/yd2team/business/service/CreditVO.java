package store.yd2team.business.service;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CreditVO {
  
    private Long cdtlnNo;
    private String custcomId;
    private String custcomName;
    private String vendId;
    private String creditGrad;
    private String cdtlnCheck;
    private Long mrtggLmt;
    private Long creditLmt;
    private Long cdtlnLmt;
    private String lmtOverCheck;
    private String yn;
    private LocalDate applcBeginDt;
    private LocalDate applcEndDt;
}
