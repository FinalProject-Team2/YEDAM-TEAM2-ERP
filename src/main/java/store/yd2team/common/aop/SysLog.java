package store.yd2team.common.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    String action();          // ac1/ac2/ac3 ...
    String msg() default "";  // 요약

    // ✅ 추가: PK를 어디서 뽑을지 메서드별로 지정
    String pkParam() default "";      // 메서드 파라미터명으로 찾기 (예: empAcctId)
    String pkField() default "";      // DTO 내부 필드명으로 찾기 (예: empAcctId)
    boolean pkFromSession() default false; // 세션(SessionDto)에서 pkField/empAcctId 등 꺼내기
        
    String onOk() default "";        // lg1
    String onFail() default "";      // lg2
    String onOtpStep() default "";   // lg6
}
