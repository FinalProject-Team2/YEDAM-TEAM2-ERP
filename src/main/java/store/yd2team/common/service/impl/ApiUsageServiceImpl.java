package store.yd2team.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.yd2team.common.mapper.SubscriptionMapper;
import store.yd2team.common.service.ApiUsageService;
import store.yd2team.common.util.LoginSession;

/**
 * API 사용량 증가 서비스 구현체.
 */
@Service
public class ApiUsageServiceImpl implements ApiUsageService {
	
	@Autowired
	private SubscriptionMapper subscriptionMapper;
	
	@Override
	@Transactional
	public void increaseApiUsageForCurrentVendor() {
		String vendId = LoginSession.getVendId();
		if (vendId == null || vendId.isEmpty()) {
			return; // 로그인 정보 없으면 아무 작업도 하지 않음
		}
		// vendId 기준으로 tb_subsp.api_use_cnt 를 1 증가
		subscriptionMapper.incrementApiUseCountByVendId(vendId);
	}
}
