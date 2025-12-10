package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class ChurnStdrVO {
		private String  churnRiskId;
		private String  custAggregateNo;
		private String  vendId;
		private String  salesChange;
		private String  TY;
		private String  stdrValue;
		private String  stdrDc;
		private String  YN;
		private Date 	creaDt;  // 생성일시
	    private String 	creaBy;  // 생성자
	    private Date 	updtDt;  // 수정일시
	    private String 	updtBy;  // 수정자
}
