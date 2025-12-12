// store.yd2team.common.aop.SysLog
package store.yd2team.common.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String action();        // INSERT / UPDATE / DELETE / LOGIN ...
    String msg() default ""; // 요약(없으면 기본 메시지 생성)
}
