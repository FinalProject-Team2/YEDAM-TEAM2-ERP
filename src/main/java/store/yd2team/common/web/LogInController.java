package store.yd2team.common.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.LoginResultDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.EmpAcctService;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.SecPolicyVO;
import store.yd2team.common.service.SmsService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class LogInController {

    final EmpAcctService empAcctService;
    final SmsService smsService;

    // OTP 기본값 (정책이 비어있을 때 대비)
    private static final int DEFAULT_OTP_VALID_MIN  = 5;  // 5분
    private static final int DEFAULT_OTP_FAIL_LIMIT = 5;  // 5회

    // ==========================
    // 1) 로그인 (ID/PW + 캡챠 + OTP 1단계)
    // ==========================
    @PostMapping("/logIn/login")
    public LoginResultDto login(@RequestParam("vendId") String vendId,
                                @RequestParam("loginId") String loginId,
                                @RequestParam("password") String password,
                                @RequestParam(value = "captchaValue", required = false) String captchaValue,
                                HttpSession session) {

        // 0) 이번 로그인 시도에서 캡챠가 필요한지 정책 + 실패횟수 기반으로 체크
        boolean captchaRequired = empAcctService.isCaptchaRequired(vendId, loginId);

        // ==========================
        // 1) 캡챠 검증 (필요한 경우에만)
        // ==========================
        if (captchaRequired) {
            String answer = (String) session.getAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER);

            if (answer == null) {
                return LoginResultDto.captchaFail("보안문자를 다시 받아주세요.");
            }

            if (captchaValue == null || captchaValue.isBlank()
                    || !answer.equalsIgnoreCase(captchaValue.trim())) {
                return LoginResultDto.captchaFail("보안문자를 정확히 입력해 주세요.");
            }

            // 캡챠 통과했으면 한 번 쓰고 제거 (재사용 방지)
            session.removeAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER);

        } else {
            // 캡챠가 필요 없는 정책인데 예전 값이 남아있으면 깔끔하게 제거
            session.removeAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER);
        }

        // OTP/계정 관련 이전 잔여 세션값 정리
        clearOtpSession(session);

        // ==========================
        // 2) 실제 로그인 서비스 호출 (ID/PW + 잠금 + 정책)
        // ==========================
        LoginResultDto result = empAcctService.login(vendId, loginId, password);

        // ---------------------------------
        // (1) 최종 로그인 성공 (OTP 미사용)
        // ---------------------------------
        if (result.isSuccess() && result.getEmpAcct() != null) {
            EmpAcctVO empAcct = result.getEmpAcct();

            SessionDto loginEmp = buildSessionEmp(empAcct);
            session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);

            log.info(">>> 로그인 + 세션 저장 완료: sessionId={}, empAcctId={}, empNm={}, deptNm={}, deptId={}, empId={}, loginId={}, vendId={}, masYn={}, bizcnd={}, addr={}, cttpc={}, hp={}",
                    session.getId(),
                    loginEmp.getEmpAcctId(), loginEmp.getEmpNm(),
                    loginEmp.getDeptNm(), loginEmp.getDeptId(),
                    loginEmp.getEmpId(), loginEmp.getLoginId(),
                    loginEmp.getVendId(), loginEmp.getMasYn(),
                    loginEmp.getBizcnd(), loginEmp.getAddr(),
                    loginEmp.getCttpc(), loginEmp.getHp());

            return result; // success=true, otpRequired=false
        }

        // ---------------------------------
        // (2) OTP가 필요한 상태 (ID/PW OK + OTP_YN = Y)
        //    → success=false, otpRequired=true, empAcct != null
        // ---------------------------------
        if (!result.isSuccess() && result.isOtpRequired() && result.getEmpAcct() != null) {
            EmpAcctVO empAcct = result.getEmpAcct();

            // 2-1) OTP 정책 조회 (otp_valid_min, otp_fail_cnt)
            SecPolicyVO policy = empAcctService.getSecPolicy(empAcct.getVendId());

            int otpValidMin  = DEFAULT_OTP_VALID_MIN;
            int otpFailLimit = DEFAULT_OTP_FAIL_LIMIT;

            if (policy != null) {
                if (policy.getOtpValidMin() != null && policy.getOtpValidMin() > 0) {
                    otpValidMin = policy.getOtpValidMin();
                }
                if (policy.getOtpFailCnt() != null && policy.getOtpFailCnt() > 0) {
                    otpFailLimit = policy.getOtpFailCnt();
                }
            }

            // 2-2) OTP 대기 중인 계정 정보 임시 저장
            session.setAttribute(SessionConst.PENDING_LOGIN_EMP, empAcct);

            // 2-3) OTP 코드 생성 + 만료시간/실패횟수 제한 저장
            String otpCode = generateOtpCode(6);
            long now = System.currentTimeMillis();
            long expireTimeMillis = now + (otpValidMin * 60L * 1000L); // 정책 기준 유효 시간

            session.setAttribute(SessionConst.LOGIN_OTP_CODE, otpCode);
            session.setAttribute(SessionConst.LOGIN_OTP_EXPIRE, expireTimeMillis);
            session.setAttribute(SessionConst.LOGIN_OTP_FAIL_CNT, 0);              // 현재 실패 횟수
            session.setAttribute(SessionConst.LOGIN_OTP_FAIL_LIMIT, otpFailLimit); // 허용 실패 횟수

            // 2-4) OTP 문자 발송 (hp → cttpc 순으로 사용)
            String targetMobile = selectOtpTargetNumber(empAcct); // hp 우선, 없으면 cttpc
            if (targetMobile != null && !targetMobile.isBlank()) {
            	// 나중에 주석으로 막을 예정
                // smsService.sendOtpSms(targetMobile, otpCode, otpValidMin);
                
                log.info(">>> [DEV ONLY] OTP 문자 발송: to={}, otpCode={}, validMin={}, failLimit={}",
                        targetMobile, otpCode, otpValidMin, otpFailLimit);
            } else {
                log.warn("OTP 문자 발송 불가 - hp/cttpc 모두 없음: empAcctId={}", empAcct.getEmpAcctId());

                log.info(">>> [DEV ONLY] OTP 생성 (문자 미발송, 번호 없음): vendId={}, loginId={}, otpCode={}, validMin={}, failLimit={}",
                        empAcct.getVendId(), empAcct.getLoginId(), otpCode, otpValidMin, otpFailLimit);
            }

            return result; // success=false, otpRequired=true
        }

        // ---------------------------------
        // (3) 일반 실패 (비밀번호 틀림/잠금/기타 사유)
        // ---------------------------------
        return result;
    }

    // ==========================
    // 2) OTP 검증 API (2단계 로그인)
    // ==========================
    @PostMapping("/logIn/otp")
    public LoginResultDto verifyOtp(@RequestParam("otpCode") String otpCode,
                                    HttpSession session) {

        String savedOtp      = (String) session.getAttribute(SessionConst.LOGIN_OTP_CODE);
        Long expireMillis    = (Long) session.getAttribute(SessionConst.LOGIN_OTP_EXPIRE);
        EmpAcctVO pendingEmp = (EmpAcctVO) session.getAttribute(SessionConst.PENDING_LOGIN_EMP);

        Integer failCntObj   = (Integer) session.getAttribute(SessionConst.LOGIN_OTP_FAIL_CNT);
        Integer failLimitObj = (Integer) session.getAttribute(SessionConst.LOGIN_OTP_FAIL_LIMIT);

        int failCnt   = (failCntObj == null ? 0 : failCntObj);
        int failLimit = (failLimitObj == null || failLimitObj <= 0 ? DEFAULT_OTP_FAIL_LIMIT : failLimitObj);

        // 1) OTP 세션정보가 없는 경우 (직접 URL 접근 / 세션 만료 등)
        if (savedOtp == null || expireMillis == null || pendingEmp == null) {
            return LoginResultDto.fail("OTP 세션 정보가 없습니다. 다시 로그인해 주세요.");
        }

        // 2) 만료 시간 체크 (otp_valid_min 기준)
        long now = System.currentTimeMillis();
        if (now > expireMillis) {
            // 만료된 OTP 정보는 지우고 재로그인 유도
            clearOtpSession(session);
            return LoginResultDto.fail("OTP 유효 시간이 지났습니다. 다시 로그인해 주세요.");
        }

        // 3) 코드 일치 여부 확인
        if (otpCode == null || !savedOtp.equals(otpCode.trim())) {
            failCnt++;
            session.setAttribute(SessionConst.LOGIN_OTP_FAIL_CNT, failCnt);

            if (failCnt >= failLimit) {
                // otp_fail_cnt 회 이상 틀리면 OTP 폐기 + 재로그인 요구
                clearOtpSession(session);
                return LoginResultDto.fail(
                        "OTP를 " + failLimit + "회 이상 잘못 입력하여 다시 로그인해 주세요."
                );
            } else {
                int remain = failLimit - failCnt;
                return LoginResultDto.fail(
                        "OTP 코드가 올바르지 않습니다. (남은 시도 횟수: " + remain + "회)"
                );
            }
        }

        // 4) OTP 검증 성공 → 최종 로그인 세션 생성
        EmpAcctVO empAcct = pendingEmp;

        SessionDto loginEmp = buildSessionEmp(empAcct);
        session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);

        log.info(">>> OTP 로그인 + 세션 저장 완료: sessionId={}, empAcctId={}, empNm={}, deptNm={}, deptId={}, empId={}, loginId={}, vendId={}, masYn={}, bizcnd={}, addr={}, cttpc={}, hp={}",
                session.getId(),
                loginEmp.getEmpAcctId(), loginEmp.getEmpNm(),
                loginEmp.getDeptNm(), loginEmp.getDeptId(),
                loginEmp.getEmpId(), loginEmp.getLoginId(),
                loginEmp.getVendId(), loginEmp.getMasYn(),
                loginEmp.getBizcnd(), loginEmp.getAddr(),
                loginEmp.getCttpc(), loginEmp.getHp());

        // 5) OTP 관련 임시 세션은 제거
        clearOtpSession(session);

        // 최종 성공 응답
        return LoginResultDto.ok(empAcct);
    }

    // ==========================
    // 내부 OTP 코드 생성 유틸 (숫자 N자리)
    // ==========================
    private String generateOtpCode(int length) {
        int max = (int) Math.pow(10, length);   // 예: length=6 → 1000000
        int num = (int) (Math.random() * max);
        return String.format("%0" + length + "d", num); // 항상 고정 길이로 0 padding
    }

    // ==========================
    // OTP 세션 정리 유틸
    // ==========================
    private void clearOtpSession(HttpSession session) {
        session.removeAttribute(SessionConst.LOGIN_OTP_CODE);
        session.removeAttribute(SessionConst.LOGIN_OTP_EXPIRE);
        session.removeAttribute(SessionConst.PENDING_LOGIN_EMP);
        session.removeAttribute(SessionConst.LOGIN_OTP_FAIL_CNT);
        session.removeAttribute(SessionConst.LOGIN_OTP_FAIL_LIMIT);
    }

    // ==========================
    // SessionDto 생성 공통 유틸
    // ==========================
    private SessionDto buildSessionEmp(EmpAcctVO empAcct) {
        SessionDto loginEmp = new SessionDto();
        loginEmp.setEmpAcctId(empAcct.getEmpAcctId());
        loginEmp.setVendId(empAcct.getVendId());
        loginEmp.setEmpId(empAcct.getEmpId());
        loginEmp.setLoginId(empAcct.getLoginId());
        loginEmp.setEmpNm(empAcct.getEmpNm());
        loginEmp.setDeptId(empAcct.getDeptId());
        loginEmp.setDeptNm(empAcct.getDeptNm());
        loginEmp.setMasYn(empAcct.getMasYn());

        // 업체/연락처 정보
        loginEmp.setBizcnd(empAcct.getBizcnd());
        loginEmp.setAddr(empAcct.getAddr());
        loginEmp.setCttpc(empAcct.getCttpc());
        loginEmp.setHp(empAcct.getHp());

        return loginEmp;
    }

    // ==========================
    // OTP 문자 발송 대상 번호 선택 (hp → cttpc 순)
    // ==========================
    private String selectOtpTargetNumber(EmpAcctVO empAcct) {
        // hp(거래처 핸드폰 번호)가 있으면 hp 우선
        if (empAcct.getHp() != null && !empAcct.getHp().isBlank()) {
            return empAcct.getHp();
        }
        // 없으면 cttpc(사원 연락처) 사용
        if (empAcct.getCttpc() != null && !empAcct.getCttpc().isBlank()) {
            return empAcct.getCttpc();
        }
        // 둘 다 없으면 null
        return null;
    }

    // ==========================
    // 로그아웃
    // ==========================
    @PostMapping("/logIn/logout")
    public LoginResultDto logout(HttpSession session) {
        session.invalidate();
        return LoginResultDto.ok();
    }

}
