package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class MonthlySalesDTO {
    private String customerId;		//고객사
    private Integer trendScore;              
    private Integer recScore;
    private Integer freqScore;
    private Integer finalScore;
    private String customerStatus;
    private String custcomName; //고객사 이름
    private String  vendId;
    private String  empId;
    
    private Date creaDt;             // CREA_DT  (문자열로 받기)
    private String creaBy;           // CREA_BY
    private Date updtDt;             // UPDT_DT
    private String updtBy;           // UPDT_BY
}