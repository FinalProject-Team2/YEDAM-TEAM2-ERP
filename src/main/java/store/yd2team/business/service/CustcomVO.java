package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CustcomVO {

	// 그리드
	private String custcomId;
    private String custcomName;
    private String bizNo;
    private String bizAddr;
    private String bizTel;
    private String bizFax;
    private String psch;
    private String pschTel;
    private String pschEmail;
    private String rpstr;
    private String rpstrTel;
    private String bsTy;
    
    // 거래구분 공통코드
    private String codeId;
    private String grpId;
    private String codeNm;
    private String yn;
    
    // 세션정보
    private String vendId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creaDt;
    private String creaBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updtDt;
    private String updtBy;
    
}
