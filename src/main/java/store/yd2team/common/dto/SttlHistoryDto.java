package store.yd2team.common.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class SttlHistoryDto {
	
	private String vendNm;   // 업체 명
	private String planNm;   // 구독 플랜 명
	private String sttlPerd; // 결제 주기 코드(b1/b2)
	private Date startDt;    // 구독 시작 일
	private Date endDt;      // 구독 종료 일
	
}// end class