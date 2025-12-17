package store.yd2team.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.MenuAuthMapper;
import store.yd2team.common.util.LoginSession;

@Component
@RequiredArgsConstructor
public class PageTitleInterceptor implements HandlerInterceptor {

    private final MenuAuthMapper menuMapper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {

        String uri = req.getRequestURI();

        // 화면 렌더링 요청만 (필요한 것만 더 추가해도 됨)
        if (uri.startsWith("/api/")
                || uri.startsWith("/assets/")
                || uri.startsWith("/emp/profile/")
                || uri.startsWith("/favicon")
        ) {
            return true;
        }

        SessionDto s = LoginSession.getLoginSession();
        String vendId = (s != null) ? s.getVendId() : null;

        String title = null;
        if (vendId != null) {
            title = menuMapper.selectMenuNameByUrl(vendId, uri);
        }

        // 못 찾으면 기본 타이틀
        req.setAttribute("pageTitle", (title != null && !title.isBlank()) ? title : "대시 보드");
        return true;
    }
}
