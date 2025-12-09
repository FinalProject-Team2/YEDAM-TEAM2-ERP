package store.yd2team.common.service;

import java.sql.Date;

import lombok.Data;

@Data
public class SttlVO {
	
	private String sttlId;// 결제ID
	private String vendId;// 업체ID
	private String subspPlanId;// 구독플랜ID
	private String paymentKey;// 결제키
	private Date creaDt;// 생성일자
	private String creaBy;// 생성자
	private Date updtDt;// 수정일자
	private String updtBy;// 수정자
	
}// end class