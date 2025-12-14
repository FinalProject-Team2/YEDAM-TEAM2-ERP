package store.yd2team.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 로그인 없이 접근 허용 (정확히 일치해야 하는 URL들)
     */
    private static final List<String> WHITE_LIST_EXACT = List.of(
            "/logIn",           // 로그인 화면
            "/error",
            "/favicon.ico",
            "/pdf",
            "/Payment"
    );
    
    // 구독 해지 계정이 접근 가능한 URL prefix
    private static final List<String> SUBS_CANCEL_ALLOWED_PREFIX = List.of(
        "/SubscriptionChoice",          // 플랜 선택
        "/Payment",                // 결제
        "/subscription/check",    // 내 구독 정보
        "/logIn/logout",       // 로그아웃은 허용
        "/subscription/payment",
        "/assets/", "/css/", "/js/", "/images/", "/webjars/"
    );

    /**
     * 로그인 없이 접근 허용 (접두사 기준)
     *  - 로그인 처리, OTP 처리, 캡차, 정적 리소스 등
     */
    private static final List<String> WHITE_LIST_PREFIX = List.of(
            "/logIn/login",         // 로그인 처리
            "/logIn/otp",           // OTP 인증
            "/logIn/otp/resend",    // OTP 재발급
            "/logIn/logout",        // 로그아웃 (세션 없으면 그냥 통과)
            "/logIn/captcha",             // 캡챠 이미지
            "/assets/",             // 정적 리소스 (CSS/JS/이미지 등)
            "/css/",
            "/js/",
            "/images/",
            "/webjars/",
            "/signUp"               // 회원가입 화면/처리 (필요하면)
    );

    private boolean isWhiteList(String requestURI) {

        // 1) 완전 일치 체크
        if (WHITE_LIST_EXACT.contains(requestURI)) {
            return true;
        }

        // 2) prefix 체크
        return WHITE_LIST_PREFIX.stream().anyMatch(uri -> requestURI.startsWith(uri));
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        
        // ✅ 1) 프로필 이미지 요청은 로그인 체크 없이 항상 통과
        if (requestURI.equals("/emp/profile/photo")) {
            return true;
        }

        log.debug("[LoginCheckInterceptor] uri={}", requestURI);

        // 1) 화이트리스트는 무조건 통과
        if (isWhiteList(requestURI)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        SessionDto loginEmp = (session != null)
                ? (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP)
                : null;

        // 2) 로그인 안 된 경우 → 로그인 화면으로 강제 이동
        if (loginEmp == null) {

            String fullUrl = requestURI + (queryString != null ? "?" + queryString : "");
            String redirectURL = URLEncoder.encode(fullUrl, StandardCharsets.UTF_8);

            log.debug("미로그인 상태 → 로그인 페이지로 redirect");
            response.sendRedirect("/logIn?redirectURL=" + redirectURL);
            return false;
        }

     // 3) 로그인은 됐는데, 임시 비밀번호(tempYn = e1)이면 비밀번호 변경을 강제
        if ("e1".equals(loginEmp.getTempYn())) {

            // 비밀번호 변경 API/정책, 로그아웃은 허용
            boolean isPwChangeApi   = requestURI.startsWith("/mypage/pwChange");
            boolean isPwPolicyApi   = requestURI.startsWith("/mypage/pwPolicyInfo");
            boolean isLogout        = requestURI.startsWith("/logIn/logout");
            
            boolean isForcePwChangePage =
                    "/".equals(requestURI)
                            && "true".equals(request.getParameter("forcePwChange"));
            
            if (!isPwChangeApi && !isPwPolicyApi && !isLogout && !isForcePwChangePage) {
                log.debug("임시 비밀번호 계정 → 대시보드로 redirect + 강제 변경 플래그");

                // 👉 새 페이지 말고, 기존 "/"로 보내되 쿼리파라미터만 추가
                response.sendRedirect("/?forcePwChange=true");
                return false;
            }
        }
	        
	     // ==========================
	     // 3.5) 구독 해지(r4) 접근 제한 + 최초 진입 제어
	     // ==========================
	     if ("r4".equals(loginEmp.getAcctSt())) {
	
	         // 1) 루트(/) 접근 시 → 무조건 구독 플랜 페이지
	         if ("/".equals(requestURI)) {
	             response.sendRedirect("/SubscriptionChoice");
	             return false;
	         }
	
	         // 2) 허용된 URL만 통과
	         boolean allowed = SUBS_CANCEL_ALLOWED_PREFIX.stream()
	                 .anyMatch(requestURI::startsWith);
	
	         if (!allowed) {
	             response.sendRedirect("/SubscriptionChoice");
	             return false;
	         }
	     }

        // 4) 정상 로그인 + tempYn != e1 → 그대로 진행
        return true;
        
    }
}
