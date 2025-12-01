package store.yd2team.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;

public class LoginSession {

	private LoginSession() {
        // 인스턴스 생성 방지
    }

    /**
     * 현재 요청의 HttpSession 반환 (없으면 null)
     */
    private static HttpSession getSession() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return null; // 웹 요청 컨텍스트가 아닌 경우
        }
        // false: 기존 세션만, 없으면 null
        return attrs.getRequest().getSession(false);
    }

    /**
     * 세션에 저장된 로그인 정보(SessionDto) 통째로 반환
     */
    public static SessionDto getLoginSession() {
        HttpSession session = getSession();
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(SessionConst.LOGIN_EMP);
        if (value instanceof SessionDto) {
            return (SessionDto) value;
        }
        return null;
    }

    // ==========================
    // 편의 메서드들
    // ==========================

    public static String getEmpAcctId() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getEmpAcctId() : null;
    }

    public static String getVendId() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getVendId() : null;
    }

    public static String getEmpId() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getEmpId() : null;
    }

    public static String getLoginId() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getLoginId() : null;
    }

    public static String getEmpNm() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getEmpNm() : null;
    }

    public static String getDeptId() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getDeptId() : null;
    }

    public static String getDeptNm() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getDeptNm() : null;
    }
	
}
