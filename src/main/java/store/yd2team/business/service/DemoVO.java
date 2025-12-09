package store.yd2team.business.service;

import java.util.Date;
import lombok.Data;

@Data
public class DemoVO {

	private Integer demoQuotatioNo;     // DEMO_QUOTATIO_NO
	private Integer potentialInfoNo;    // POTENTIAL_INFO_NO
	private String vendId;           // VEND_ID
	private Date demoQuotatioDt;     // DEMO_QUOTATIO_DT
	private String demoManager;      // DEMO_MANAGER
	private String demoDc;           // DEMO_DC
	private String custReaction;     // CUST_REACTION
	private String samplePrivide;    // SAMPLE_PRIVIDE
	private Long custQy;             // CUST_QY
	private Long custDiscount;       // CUST_DISCOUNT
	private Date custDelivery;       // CUST_DELIVERY
	private Date creaDt;             // CREA_DT
	private String creaBy;           // CREA_BY
	private Date updtDt;             // UPDT_DT
	private String updtBy;           // UPDT_BY

}
