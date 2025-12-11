package store.yd2team.common.service;

/**
 * API 사용량 증가 관련 서비스 인터페이스.
 */
public interface ApiUsageService {
	/**
	 * 현재 로그인한 사용자의 vendId 기준으로 tb_subsp.api_use_cnt 를 1 증가시킨다.
	 */
	void increaseApiUsageForCurrentVendor();
}
