package store.yd2team.business.service;

import lombok.Data;

@Data
public class ProductVO {

	private String productId;
	private String vendId;
    private String productName;
    private String unit;
    private String basisPurcAmt;
    private String basisSaleAmt;
    private String yn;
    private String rm;
    private String creaDt;
}
