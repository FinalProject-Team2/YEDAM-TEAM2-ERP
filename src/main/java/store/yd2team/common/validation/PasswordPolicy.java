package store.yd2team.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * tb_sec_policy 기반 비밀번호 정책 검증용 커스텀 어노테이션
 */
@Documented
@Target(ElementType.TYPE) // 클래스에 붙이기
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordPolicyValidator.class)
public @interface PasswordPolicy {

    String message() default "비밀번호 정책을 만족하지 않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}