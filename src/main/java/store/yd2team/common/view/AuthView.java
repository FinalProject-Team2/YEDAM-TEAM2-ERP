package store.yd2team.common.view;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.util.LoginSession;

@Slf4j
@Component("authView")
public class AuthView {
	

   private SessionDto getSession() {
        SessionDto s = LoginSession.getLoginSession();
        if (s != null) {
            log.debug("[AuthView] empAcctId={}, empId={}, masYn={}, roleIds={}",
                    s.getEmpAcctId(), s.getEmpId(), s.getMasYn(), s.getRoleIds());
        } else {
            log.debug("[AuthView] session is null");
        }
        return s;
    }

   private boolean hasRole(List<String> roles, String role) {
	    if (roles == null) return false;
	    return roles.contains(role) || roles.contains("ROLE_" + role);
	}

	private boolean isOperator(SessionDto s) {
	    if (s == null) return false;

	    List<String> roles = s.getRoleIds();
	    return hasRole(roles, "OPERATOR")
	        || hasRole(roles, "ADMIN")
	        || hasRole(roles, "SYS_ADMIN");
	}

    private MenuAuthDto getMenuAuth(String menuId) {
        SessionDto s = getSession();
        if (s == null) return null;

        Map<String, MenuAuthDto> map = s.getMenuAuthMap();
        if (map == null) return null;

        return map.get(menuId);
    }

    public boolean canRead(String menuId) {
        SessionDto s = getSession();
        if (isOperator(s)) return true;        // ✅ 운영자면 무조건 허용
        MenuAuthDto auth = getMenuAuth(menuId);
        return auth != null && auth.isReadable();
    }

    public boolean canWrite(String menuId) {
        SessionDto s = getSession();
        if (isOperator(s)) return true;        // ✅ 운영자면 무조건 허용
        MenuAuthDto auth = getMenuAuth(menuId);
        return auth != null && auth.isWritable();
    }

    public boolean canDelete(String menuId) {
        SessionDto s = getSession();
        if (isOperator(s)) return true;        // ✅ 운영자면 무조건 허용
        MenuAuthDto auth = getMenuAuth(menuId);
        return auth != null && auth.isDeletable();
    }
}

