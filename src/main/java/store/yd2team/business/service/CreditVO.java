package store.yd2team.business.service;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class CreditVO {
	
   // --- 여신한도 테이블(tb_cdtln_lmt)
   private String cdtlnNo;
   private String vendId;
   private String custcomId;
   private String custcomName;
   private String creditGrad;
   private String cdtlnCheck;
   private Long mrtggLmt;
   private Long creditLmt;
   private Long cdtlnLmt;
   private Long bondBaln;
   private String lmtoverCheck;
   private String yn;
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate applcBeginDt;
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate applcEndDt;
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate lastEvalDt;
   private int useRate;
   
   
   // --- 신규 추가 (여신관리 기능 강화용)
   private Integer creditMm;      // 여신 개월 수
   private String badCreditYn;    // 악성여신 여부
   private String shipmntStop;     // 출하정지 여부
   private Integer turnoverDays;  // 회전일수
   
   // --- 미수/연체 현황 계산 값
   private Long remainAmt;        // 미수잔액
   private Integer maxOverdueMm;  // 최대 연체개월
   
   // --- 고객사 상세 정보(tb_custcom)
   private String bizAddr;
   private String bizTel;
   private String bizFax;
   private String psch;
   private String pschTel;
   private String pschEmail;
   
   // 원금잔액
   private Long prinsumBaln;
   
   // 세션
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate creaDt;
   private String creaBy;
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private LocalDate updtDt;
   private String updtBy;
   
}



