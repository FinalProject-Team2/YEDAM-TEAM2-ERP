package store.yd2team.common.dto;

import java.sql.Date;

import lombok.Data;

/**
 * 구독 정보 + 사용량을 한 번에 내려주는 DTO
 */

@Data
public class SubscriptionUsageDto {
	// tb_subsp_plan 기준 정보
	private String planNm;      // 구독 플랜 명
	private Date creaDt;        // (기존) 플랜 생성일 기준
	private Date startDt;       // tb_subsp.start_dt (실제 구독 시작일)
	private Date endDt;         // tb_subsp.end_dt   (실제 구독 종료일)
	private String sttlPerd;    // 결제주기
	private Long acctIssuCnt;   // 계정 발급 허용 수
	private Long apiIssuCnt;    // 월 API 허용 수

	// tb_subsp 기준 사용량
	private Long acctUseCnt;    // 발급한 계정 수
	private Long apiUseCnt;     // 사용한 API 수
}