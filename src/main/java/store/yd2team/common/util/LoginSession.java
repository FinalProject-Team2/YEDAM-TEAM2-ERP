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
    
    public static String getMasYn() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getMasYn() : null;
    }
    
    public static String getBizcnd() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getBizcnd() : null;
    }
    
    public static String getAddr() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getAddr() : null;
    }
	
    public static String getCttpc() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getCttpc() : null;
    }
    
    public static String getHp() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getHp() : null;
    }
    
    public static String getEmail() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getEmail() : null;
    }
    
    public static String getProofPhoto() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getProofPhoto() : null;
    }
    
    
    // ==========================
    // 1) 역할 / 권한 Getter
    // ==========================
    public static java.util.List<String> getRoleIds() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getRoleIds() : null;
    }

    public static java.util.Set<String> getAuthCodes() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getAuthCodes() : null;
    }

    // ==========================
    // 2) 역할 체크 헬퍼
    // ==========================
    public static boolean hasRole(String roleId) {
        SessionDto s = getLoginSession();
        if (s == null || s.getRoleIds() == null) return false;
        return s.getRoleIds().contains(roleId);
    }

    public static boolean hasAnyRole(String... roleIds) {
        SessionDto s = getLoginSession();
        if (s == null || s.getRoleIds() == null) return false;

        java.util.Set<String> myRoles = new java.util.HashSet<>(s.getRoleIds());
        for (String r : roleIds) {
            if (myRoles.contains(r)) {
                return true;
            }
        }
        return false;
    }

    // ==========================
    // 3) 권한 코드 체크 헬퍼 (나중에 활용)
    // ==========================
    public static boolean hasAuth(String authCode) {
        SessionDto s = getLoginSession();
        if (s == null || s.getAuthCodes() == null) return false;
        return s.getAuthCodes().contains(authCode);
    }

    public static boolean hasAnyAuth(String... authCodes) {
        SessionDto s = getLoginSession();
        if (s == null || s.getAuthCodes() == null) return false;

        java.util.Set<String> myAuths = s.getAuthCodes();
        for (String code : authCodes) {
            if (myAuths.contains(code)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getJobNm() {
        SessionDto s = getLoginSession();
        return (s != null) ? s.getJobNm() : null;
    }
}
