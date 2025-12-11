// store.yd2team.common.aop.SysLogAspect
package store.yd2team.common.aop;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.SystemLogService;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SysLogAspect {

    private final SystemLogService systemLogService;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {

        Object result = null;
        Throwable ex = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            ex = e;
            throw e;
        } finally {
            if (ex == null) { // 예외 없이 정상 완료된 경우만 로그 기록
                try {
                    writeLog(joinPoint, sysLog, result);
                } catch (Exception logEx) {
                    log.error("시스템 로그 기록 중 오류", logEx);
                }
            }
        }
    }

    private void writeLog(ProceedingJoinPoint joinPoint, SysLog sysLog, Object result) {

        // 1) 클래스 수준 설정 읽기 (@SysLogConfig)
        Class<?> targetClass = joinPoint.getTarget().getClass();
        SysLogConfig config = targetClass.getAnnotation(SysLogConfig.class);
        if (config == null) {
            log.debug("@SysLogConfig 없는 클래스 → 로그 스킵: {}", targetClass.getName());
            return;
        }

        String module = config.module();   // HR / COMMON / SALES
        String table  = config.table();    // TB_EMP_ACCT 등
        String pkParamName = config.pkParam(); // empAcctId 등

        // 2) SessionDto 찾기
        SessionDto session = findSessionFromArgs(joinPoint.getArgs());
        if (session == null) {
            session = findSessionFromHttpSession();
        }
        if (session == null) {
            log.debug("SessionDto 없음 → 로그 스킵");
            return;
        }

        // 3) PK 값 찾기 (파라미터 → 리턴값 순으로 검색)
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = sig.getParameterNames();
        Object[] args = joinPoint.getArgs();

        String pkValue = resolvePk(pkParamName, paramNames, args, result);

        // 4) 요약 메시지
        String summary = sysLog.msg();
        if (summary == null || summary.isBlank()) {
            summary = String.format("%s %s on %s (pk=%s)",
                    module, sysLog.action(), table, pkValue);
        }

        // 5) 실제 로그 기록
        systemLogService.writeLog(
                session,
                module,
                sysLog.action(),
                table,
                pkValue,
                summary
        );
    }

    private String resolvePk(String pkParamName,
                             String[] paramNames,
                             Object[] args,
                             Object result) {

        // 1) @SysLogConfig.pkParam 이 지정되어 있으면 우선 사용
        if (pkParamName != null && !pkParamName.isBlank() &&
                paramNames != null && args != null) {

            for (int i = 0; i < paramNames.length; i++) {
                if (pkParamName.equals(paramNames[i])) {
                    return String.valueOf(args[i]);
                }
            }
        }

        // 2) 지정 안 했으면 파라미터 중에서 xxxId 자동 탐색
        if (paramNames != null && args != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if (paramNames[i].toLowerCase().endsWith("id")) {
                    return String.valueOf(args[i]);
                }
            }
        }

        // 3) 리턴값에서 getEmpAcctId(), getId() 같은 메서드 탐색 (옵션)
        if (result != null) {
            try {
                var m = result.getClass().getMethod("getEmpAcctId");
                Object v = m.invoke(result);
                if (v != null) return String.valueOf(v);
            } catch (Exception ignored) {}
            try {
                var m = result.getClass().getMethod("getId");
                Object v = m.invoke(result);
                if (v != null) return String.valueOf(v);
            } catch (Exception ignored) {}
        }

        return null;
    }

    private SessionDto findSessionFromArgs(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof SessionDto) {
                return (SessionDto) arg;
            }
        }
        return null;
    }

    private SessionDto findSessionFromHttpSession() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;

        HttpSession httpSession = attrs.getRequest().getSession(false);
        if (httpSession == null) return null;

        Object obj = httpSession.getAttribute(SessionConst.LOGIN_EMP);
        if (obj instanceof SessionDto) {
            return (SessionDto) obj;
        }
        return null;
    }
}
