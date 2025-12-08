package store.yd2team.business.service;

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
}
