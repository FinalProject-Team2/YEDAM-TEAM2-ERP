package store.yd2team.common.aop;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogConfig {

    String module();   // HR / COMMON / SALES ...
    String table();    // TB_EMP_ACCT / TB_RCIPT ...
    String pkParam() default ""; // PK로 쓸 파라미터/필드 이름 (empAcctId, empId 등)
}
