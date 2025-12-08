package store.yd2team.common.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SecPolicyService secPolicyService;

    @PostMapping("/api/session/extend")
    public ExtendSessionResponse extendSession(HttpSession session) {

        // 1) 로그인 여부 체크
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        if (loginEmp == null) {
            // ★ 세션 만료 상태 → JS 에서 data.code === 'SESSION_EXPIRED' 로 체크
            return ExtendSessionResponse.fail("SESSION_EXPIRED",
                    "세션이 만료되었거나 로그인 상태가 아닙니다.");
        }

        // 2) 세션에 저장된 timeout 값을 우선 사용
        Integer timeoutMin = (Integer) session.getAttribute(SessionConst.SESSION_TIMEOUT_MIN);

        // 3) 없으면 정책 다시 조회해서 세션에 다시 세팅
        if (timeoutMin == null) {
            SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(loginEmp.getVendId());

            if (policy != null && policy.getSessionTimeoutMin() != null) {
                timeoutMin = policy.getSessionTimeoutMin();
            } else {
                // 정책에도 값이 없으면 안전하게 기본 30분
                timeoutMin = 30;
            }

            // 세션에 정책 값 다시 저장 (JS 쪽에서도 이 값 사용)
            session.setAttribute(SessionConst.SESSION_TIMEOUT_MIN, timeoutMin);

            String timeoutAction = (policy != null ? policy.getTimeoutAction() : null);
            if (timeoutAction == null || timeoutAction.isBlank()) {
                timeoutAction = "t1"; // 기본값
            }
            session.setAttribute(SessionConst.SESSION_TIMEOUT_ACTION, timeoutAction);
        }

        // 4) 실제 서버 세션 타임아웃 연장 (초 단위)
        session.setMaxInactiveInterval(timeoutMin * 60);
        
        long lastAccessed = session.getLastAccessedTime();     // ms
        int maxInactiveSec = session.getMaxInactiveInterval(); // sec
        long expireAtMs = lastAccessed + (maxInactiveSec * 1000L);
        long serverNowMs = System.currentTimeMillis();
        
        log.info("세션 연장: sessionId={}, vendId={}, loginId={}, timeout={}분",
                session.getId(), loginEmp.getVendId(), loginEmp.getLoginId(), timeoutMin);

        // 프론트 쪽에서 timeoutMin 다시 받아서 타이머 재설정 가능하게 응답
        return ExtendSessionResponse.ok(timeoutMin, expireAtMs, serverNowMs);
    }

    /**
     * 세션 연장 응답 DTO (record)
     */
    public record ExtendSessionResponse(
    		boolean success,
            String code,
            String message,
            Integer timeoutMin,
            Long expireAtMs,
            Long serverNowMs
    ) {

    	// 정상 연장
        public static ExtendSessionResponse ok(Integer timeoutMin,
                                               long expireAtMs,
                                               long serverNowMs) {
            return new ExtendSessionResponse(true, "OK",
                    "세션이 연장되었습니다.",
                    timeoutMin,
                    expireAtMs,
                    serverNowMs);
        }

        // 실패
        public static ExtendSessionResponse fail(String code, String message) {
            return new ExtendSessionResponse(false, code, message,
                    null, null, null);
        }
    }
}
