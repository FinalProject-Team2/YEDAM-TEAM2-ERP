package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class churnRiskVO {
	  private int custAggregateNo; 	//고객집계번호
	  private String vendId;		//거래처ID
	  private int churnRiskScore;	//이탈위험지수
	  private int salesChange;		//매출변동
	  private int avgPurcCycle;		//평균구매주기
	  private Date purcStopDt;		//마지막거래일
	  
}
