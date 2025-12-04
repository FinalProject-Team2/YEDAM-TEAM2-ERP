package store.yd2team.business.service;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesChangeDTO {
    private String custcomId;
    private String ym;
    private BigDecimal monthlySales;
    private BigDecimal prevSales;
    private Double changeRate;	//%
    }

