package store.yd2team.business.service;

import lombok.Data;

@Data
public class CustcomVO {

	// 그리드
	private String custcomId;
    private String custcomName;
    private String vendId;
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
    private String creaDt;
    
    // 거래구분 공통코드
    private String codeId;
    private String grpId;
    private String codeNm;
    private String yn;
    
}
