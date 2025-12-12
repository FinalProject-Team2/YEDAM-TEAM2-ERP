package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class LeadVO {

    private Integer leadNo;
    private Integer potentialInfoNo;
    private String	vendId;
    private String	leadDt;
    private String	leadManager;
    private String	requiredDt;
    private String	competitorYn;
    private String	budgetAvailYn;
    
    private Date creaDt;             // CREA_DT  (문자열로 받기)
    private String creaBy;           // CREA_BY
    private Date updtDt;             // UPDT_DT
    private String updtBy;           // UPDT_BY;
}
