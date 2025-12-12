package store.yd2team.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP 설정: @Aspect 사용을 활성화한다.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
	// 추가 설정이 필요하면 여기에 작성
}
