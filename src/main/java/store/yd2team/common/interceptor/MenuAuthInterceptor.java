package store.yd2team.common.interceptor;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.util.UrlUtil;

@Component
public class MenuAuthInterceptor implements HandlerInterceptor {
	
  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    HttpSession session = req.getSession(false);
    SessionDto loginEmp = (session == null) ? null : (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
    if (loginEmp == null) return true; // 로그인 체크는 다른 인터셉터가 함
    
    if ("e1".equals(loginEmp.getOprtrYn())) {
        return true;
    }

    String uri = UrlUtil.normalize(req.getRequestURI());

    Map<String, MenuAuthDto> byUrl = loginEmp.getMenuAuthByUrl();
    if (byUrl == null) return true;

    MenuAuthDto auth = byUrl.get(uri);

    // 메뉴 등록 안 된 URL이면 통과(원하면 여기서 막을 수도 있음)
    if (auth == null) return true;

    // 조회 권한 없으면 403
    if (!auth.isReadable()) {
        res.sendError(403);
        return false;
    }

    return true;
  }
}

