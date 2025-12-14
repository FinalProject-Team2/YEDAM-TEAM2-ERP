package store.yd2team.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpLoginResultDto;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.OprtrAcctVO;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.SystemLogService;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SysLogAspect {

	private final SystemLogService systemLogService;

	
	
	@Around("@annotation(sysLog)")
	public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
		
		log.info(">>> SysLogAspect HIT: {}", joinPoint.getSignature());
	    log.info(">>> SysLog meta: action={}, pkParam={}, pkField={}, pkFromSession={}",
	            sysLog.action(), sysLog.pkParam(), sysLog.pkField(), sysLog.pkFromSession());
		
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
	    Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
	    SysLogConfig config = targetClass.getAnnotation(SysLogConfig.class);

	    if (config == null) {
	        log.debug("@SysLogConfig 없는 클래스 → 로그 스킵: {}", targetClass.getName());
	        return;
	    }

	    String module = config.module();
	    String table = config.table();
	    String pkParamName = config.pkParam();

	    // 2) SessionDto 찾기 (로그인 같은 경우엔 세션이 없을 수 있음!)
	    SessionDto session = findSessionFromArgs(joinPoint.getArgs());
	    if (session == null) session = findSessionFromHttpSession();
	    
	    if (session == null) {
	        session = buildSessionFromResultIfPossible(result);
	    }
	    
	    if (session == null) {
	        log.debug("SessionDto 없음(로그인 전 포함) → 로그 스킵");
	        return;
	    }

	    // 3) PK 값 찾기
	    MethodSignature sig = (MethodSignature) joinPoint.getSignature();
	    String[] paramNames = sig.getParameterNames();
	    Object[] args = joinPoint.getArgs();

	    String pkValue = resolvePkV2(sysLog, pkParamName, paramNames, args, result, session);
	    if (pkValue == null || pkValue.isBlank()) pkValue = "-";

	    // ✅ 4) action 결정 (기본 action + result 기반 override)
	    String action = sysLog.action();

	    if (result instanceof store.yd2team.common.dto.EmpLoginResultDto loginResult) {
	        if (loginResult.isSuccess()) {
	            if (sysLog.onOk() != null && !sysLog.onOk().isBlank()) action = sysLog.onOk();
	        } else if (loginResult.isOtpRequired()) {
	            if (sysLog.onOtpStep() != null && !sysLog.onOtpStep().isBlank()) action = sysLog.onOtpStep();
	        } else {
	            if (sysLog.onFail() != null && !sysLog.onFail().isBlank()) action = sysLog.onFail();
	        }
	    }
	    
	    if ("e1".equals(session.getOprtrYn())) {
	        // 운영자 로그
	        // emp_acct_id는 FK 충돌 나므로 비움
	        session.setEmpAcctId(null);

	        // oprtr_acct_id는 empAcctId 자리에 들어있던 값 사용
	        session.setOprtrAcctId(session.getEmpId() != null
	                ? session.getEmpId()
	                : session.getLoginId());
	    } else {
	        // 사원 로그
	        session.setOprtrAcctId(null);
	    }
	    
	    // ✅ 5) summary는 최종 action 기준으로 만들기
	    String summary = sysLog.msg();
	    if (summary == null || summary.isBlank()) {
	        summary = String.format("%s %s on %s (pk=%s)", module, action, table, pkValue);
	    }

	    // ✅ 6) 실제 로그 기록 (항상 호출!)
	    systemLogService.writeLog(session, module, action, table, pkValue, summary);

	    log.info(">>> session={}", session);
	}

	
	private SessionDto findSessionFromArgs(Object[] args) {
		if (args == null)
			return null;
		for (Object arg : args) {
			if (arg instanceof SessionDto) {
				return (SessionDto) arg;
			}
		}
		return null;
	}

	private SessionDto findSessionFromHttpSession() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs == null)
			return null;

		HttpSession httpSession = attrs.getRequest().getSession(false);
		if (httpSession == null)
			return null;

		Object obj = httpSession.getAttribute(SessionConst.LOGIN_EMP);
		if (obj instanceof SessionDto) {
			return (SessionDto) obj;
		}
		return null;
	}

	private String resolvePkV2(SysLog sysLog, String classPkParam, String[] paramNames, Object[] args, Object result,
			SessionDto session) {

// 1) 메서드: pkParam(파라미터명) 우선
		if (sysLog.pkParam() != null && !sysLog.pkParam().isBlank()) {
			String v = resolveByParamName(sysLog.pkParam(), paramNames, args);
			if (v != null)
				return v;
		}

// 2) 메서드: pkField(DTO 내부 필드명) 우선
		if (sysLog.pkField() != null && !sysLog.pkField().isBlank()) {
			String v = resolveByFieldInArgs(sysLog.pkField(), args);
			if (v != null)
				return v;
		}

// 3) 메서드: 세션에서 가져오기
		if (sysLog.pkFromSession()) {
// pkField가 있으면 그걸로, 없으면 empAcctId 기본
			String key = (sysLog.pkField() != null && !sysLog.pkField().isBlank()) ? sysLog.pkField() : "empAcctId";
			String v = resolveFromSession(key, session);
			if (v != null)
				return v;
		}

// 4) 클래스(@SysLogConfig.pkParam) 사용
		if (classPkParam != null && !classPkParam.isBlank()) {
			String v = resolveByParamName(classPkParam, paramNames, args);
			if (v != null)
				return v;
		}

// 5) 기존 자동 탐색(끝이 id인 파라미터)
		if (paramNames != null && args != null) {
			for (int i = 0; i < paramNames.length; i++) {
				if (paramNames[i] != null && paramNames[i].toLowerCase().endsWith("id")) {
					return String.valueOf(args[i]);
				}
			}
		}

// 6) 리턴값 getter 탐색
		String v = resolveFromResult(result);
		if (v != null)
			return v;

// 7) 마지막: 세션 empAcctId fallback
		return resolveFromSession("empAcctId", session);
	}

	private String resolveByParamName(String pkParamName, String[] paramNames, Object[] args) {
		if (paramNames == null || args == null)
			return null;
		for (int i = 0; i < paramNames.length; i++) {
			if (pkParamName.equals(paramNames[i])) {
				Object val = args[i];
				return val == null ? null : String.valueOf(val);
			}
		}
		return null;
	}

	private String resolveByFieldInArgs(String fieldName, Object[] args) {
		if (args == null)
			return null;

		for (Object arg : args) {
			if (arg == null)
				continue;

// getter 우선: getEmpAcctId()
			String getter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			try {
				var m = arg.getClass().getMethod(getter);
				Object v = m.invoke(arg);
				if (v != null)
					return String.valueOf(v);
			} catch (Exception ignored) {
			}

// field 직접 접근(있으면)
			try {
				var f = arg.getClass().getDeclaredField(fieldName);
				f.setAccessible(true);
				Object v = f.get(arg);
				if (v != null)
					return String.valueOf(v);
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	private String resolveFromSession(String fieldName, SessionDto session) {
		if (session == null)
			return null;

		String getter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		try {
			var m = session.getClass().getMethod(getter);
			Object v = m.invoke(session);
			return v == null ? null : String.valueOf(v);
		} catch (Exception ignored) {
		}

// 대표 기본값
		if ("empAcctId".equals(fieldName))
			return session.getEmpAcctId();
		if ("empId".equals(fieldName))
			return session.getEmpId();

		return null;
	}

	private String resolveFromResult(Object result) {
		if (result == null)
			return null;
		
	    try {
	        var m = result.getClass().getMethod("getEmpAcct");
	        Object empAcct = m.invoke(result);
	        if (empAcct != null) {
	            var m2 = empAcct.getClass().getMethod("getEmpAcctId");
	            Object v = m2.invoke(empAcct);
	            if (v != null)
	                return String.valueOf(v);
	        }
	    } catch (Exception ignored) {
	    }

// 기존 로직 유지 + 조금 확장 가능
		try {
			var m = result.getClass().getMethod("getEmpAcctId");
			Object v = m.invoke(result);
			if (v != null)
				return String.valueOf(v);
		} catch (Exception ignored) {
		}

		try {
			var m = result.getClass().getMethod("getId");
			Object v = m.invoke(result);
			if (v != null)
				return String.valueOf(v);
		} catch (Exception ignored) {
		}

		return null;
	}
	
	private SessionDto buildSessionFromResultIfPossible(Object result) {
	    if (result == null) return null;

	    if (result instanceof EmpLoginResultDto loginResult) {

	        // 1) 사원
	        if (loginResult.getEmpAcct() != null) {
	            EmpAcctVO acct = loginResult.getEmpAcct();

	            SessionDto s = new SessionDto();
	            s.setEmpAcctId(acct.getEmpAcctId());
	            s.setVendId(acct.getVendId());
	            s.setEmpId(acct.getEmpId());
	            s.setLoginId(acct.getLoginId());
	            s.setEmpNm(acct.getEmpNm());
	            s.setOprtrYn("e2");
	            return s;
	        }

	        // 2) 운영자
	        if (loginResult.getOprtrAcct() != null) {
	            OprtrAcctVO oa = loginResult.getOprtrAcct();

	            SessionDto s = new SessionDto();
	            s.setOprtrAcctId(oa.getOprtrAcctId());
	            s.setLoginId(oa.getLoginId());
	            s.setEmpNm("운영자");     // 있으면 그걸로
	            s.setVendId("SYSTEM");          // 운영자면 고정값 정책이면 OK
	            s.setOprtrYn("e1");
	            return s;
	        }
	    }
	    return null;
	}
}
