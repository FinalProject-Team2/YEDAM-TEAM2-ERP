package store.yd2team.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 사용량 증가를 위해 사용하는 AOP용 커스텀 어노테이션.
 * 이 어노테이션이 붙은 메서드가 호출될 때마다 tb_subsp.api_use_cnt 를 +1 한다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IncreaseApiUsage {
}
