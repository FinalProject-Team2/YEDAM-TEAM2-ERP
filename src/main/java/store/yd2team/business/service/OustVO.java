package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class OustVO {
	// 출하지시서 등록용
    private String oustId;          // 출하지시ID
    private String soId;            // 주문서ID
    private String vendId;          // 회사코드(FK)
    private Integer oustQy;         // 출하수량(필요시)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate oustDueDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate oustPlanDt;      // 출하예정일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate oustRequestDt;   // 출하요청일
    private String shipAddr;        // 배송지
    private String rm;              // 요청사항
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creaDt;          // 생성일
    private String creaBy;          // 생성자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updtDt;          // 수정일
    private String updtBy;          // 수정자
    
    // 출하지시서 조회
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueEnd;
    private String custcomName;
    private String psch;
    private String pschTel;
    
}