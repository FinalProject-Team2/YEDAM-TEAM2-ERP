package store.yd2team.business.service;

import java.util.Date;
import lombok.Data;

@Data
public class DemoVO {

	private Integer demoQuotatioNo;  // 데모 및 견적 번호 DEMO_QUOTATIO_NO
	private Integer potentialInfoNo; // 잠재정보번호 POTENTIAL_INFO_NO
	private String vendId;           // 거래처번호 VEND_ID
	private String demoQuotatioDt;     // 날짜 DEMO_QUOTATIO_DT
	private String demoManager;      // 데모담당자 DEMO_MANAGER
	private String demoDc;           // 기능설명 DEMO_DC
	private String custReaction;     // 고객반응 CUST_REACTION
	private String samplePrivide;    // 샘플 SAMPLE_PRIVIDE
	private Long custQy;             // 수량 CUST_QY
	private Long custDiscount;       // 할인 CUST_DISCOUNT
	private Date custDelivery;       // 납기 CUST_DELIVERY
	
	private Date creaDt;             // CREA_DT
	private String creaBy;           // CREA_BY
	private Date updtDt;             // UPDT_DT
	private String updtBy;           // UPDT_BY

}
