package store.yd2team.common.service;

import java.sql.Date;

import lombok.Data;

@Data
public class subscriptionPlanVO {
	
	private String subspPlanId;// 구독플랜ID
	private String planNm;// 플랜명
	private long acctIssuCnt;// 계정발급수
	private long apiIssuCnt;// API발급수
	private long amt;// 금액
	private String sttlPerd;// 결제주기
	private Date applcDt;// 적용일자
	private String yn;// 사용여부
	private Date creaDt;// 생성일자
	private String creaBy;// 생성자
	private Date updtDt;// 수정일자
	private String updtBy;// 수정자
	
}// end class