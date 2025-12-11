package store.yd2team.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import store.yd2team.common.service.ApiUsageService;

/**
 * @IncreaseApiUsage 가 붙은 메서드 실행 후에 tb_subsp.api_use_cnt 를 1 증가시키는 Aspect.
 */
@Aspect
@Component
public class ApiUsageAspect {
	
	@Autowired
	private ApiUsageService apiUsageService;
	
	@AfterReturning("@annotation(store.yd2team.common.aop.IncreaseApiUsage)")
	public void increaseApiUseCount(JoinPoint joinPoint) {
		// 실제 증가 로직은 서비스에서 처리 (세션의 vendId 기준)
		apiUsageService.increaseApiUsageForCurrentVendor();
	}
}
