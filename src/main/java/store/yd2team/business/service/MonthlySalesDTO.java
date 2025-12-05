package store.yd2team.business.service;

import lombok.Data;

@Data
public class MonthlySalesDTO {
    private String customerId;		//고객사
    private Integer trendScore;              
    private Integer recScore;
    private Integer freqScore;
    private Integer finalScore;
    private String customerStatus;
}