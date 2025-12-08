package store.yd2team.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class SessionTimeoutAdvice {

    @ModelAttribute
    public void addSessionTimeoutAttributes(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        // 로그인 안 된 세션이면 굳이 노출 안 함
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        if (loginEmp == null) {
            return;
        }

        // maxInactiveInterval: 초 단위
        int maxInactiveSec = session.getMaxInactiveInterval();
        if (maxInactiveSec <= 0) {
            return;
        }

        long lastAccessedTime = session.getLastAccessedTime(); // ms
        long expireAtMs = lastAccessedTime + (maxInactiveSec * 1000L);
        long serverNowMs = System.currentTimeMillis();

        // 템플릿에서 사용하기 위한 attribute
        model.addAttribute("SESSION_EXPIRE_AT", expireAtMs);
        model.addAttribute("SERVER_NOW_MS", serverNowMs);

        // (선택) 세션에 이미 있는 정책 값도 Model에 복사해두면 편함
        Object timeoutMinAttr = session.getAttribute(SessionConst.SESSION_TIMEOUT_MIN);
        if (timeoutMinAttr != null) {
            model.addAttribute("SESSION_TIMEOUT_MIN", timeoutMinAttr);
        }
        Object timeoutActionAttr = session.getAttribute(SessionConst.SESSION_TIMEOUT_ACTION);
        if (timeoutActionAttr != null) {
            model.addAttribute("SESSION_TIMEOUT_ACTION", timeoutActionAttr);
        }
        
        model.addAttribute("SESSION_TIMER_ENABLED", true);
    }
}
