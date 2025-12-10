package store.yd2team.common.view;

import java.util.Map;

import org.springframework.stereotype.Component;

import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.util.LoginSession;

@Component("authView")
public class AuthView {
	
	// 공통: 현재 로그인 세션 가져오기
    private SessionDto getSession() {
        return LoginSession.getLoginSession();
    }

    // 공통: 메뉴 권한 1건 가져오기 (없으면 null)
    private MenuAuthDto getMenuAuth(String menuId) {
        SessionDto s = getSession();
        if (s == null) return null;

        Map<String, MenuAuthDto> map = s.getMenuAuthMap();
        if (map == null) return null;

        return map.get(menuId);
    }

    // ==========================
    // 메뉴별 권한 체크 메서드들
    // ==========================

    /** 조회 권한 여부 */
    public boolean canRead(String menuId) {
        MenuAuthDto auth = getMenuAuth(menuId);
        return auth != null && auth.isReadable();
    }

    /** 저장 권한 여부 (등록+수정 포함) */
    public boolean canWrite(String menuId) {
        MenuAuthDto auth = getMenuAuth(menuId);
        return auth != null && auth.isWritable();
    }

    /** 삭제 권한 여부 */
    public boolean canDelete(String menuId) {
        MenuAuthDto auth = getMenuAuth(menuId);
        return auth != null && auth.isDeletable();
    }

}
