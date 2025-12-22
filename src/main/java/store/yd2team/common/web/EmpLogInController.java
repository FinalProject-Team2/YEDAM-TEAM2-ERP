package store.yd2team.common.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpLoginResultDto;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.MenuAuthMapper;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.EmpLoginService;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;
import store.yd2team.common.service.SmsService;
import store.yd2team.common.util.LoginSessionBuilder;

@RequiredArgsConstructor
@RestController
@Slf4j
public class EmpLogInController {

    final EmpLoginService empAcctService;
    final SmsService smsService;
    final SecPolicyService secPolicyService;
    final MenuAuthMapper menuAuthMapper;
    final LoginSessionBuilder loginSessionBuilder;

    // OTP 기본값 (정책이 비어있을 때 대비)
    private static final int DEFAULT_OTP_VALID_MIN  = 5;  // 5분
    private static final int DEFAULT_OTP_FAIL_LIMIT = 5;  // 5회

    // ==========================
    // 로그인 (ID/PW + 캡챠 + OTP 1단계)
    // ==========================
    @PostMapping("/logIn/login")
    public EmpLoginResultDto login(@RequestParam("vendId") String vendId,
                                   @RequestParam("loginId") String loginId,
                                   @RequestParam("password") String password,
                                   @RequestParam(value = "captchaValue", required = false) String captchaValue,
                                   HttpSession session) {

        // 이번 로그인 시도에서 캡챠가 필요한지 정책 + 실패횟수 기반으로 체크
        boolean captchaRequired = empAcctService.isCaptchaRequired(vendId, loginId);

        // ==========================
        // 캡챠 검증 (필요한 경우에만)
        // ==========================
        if (captchaRequired) {
            String answer = (String) session.getAttribute(SessionConst.LOGIN_CAPTCHA_ANSWER);

            if (answer == null) {
                return EmpLoginResultDto.captchaFail("보안문자를 다시 받아주세요.");
            }

            if (captchaValue == null || captchaValue.isBlank()
                    || !answer.equalsIgnoreCase(captchaValue.trim())) {
                return EmpLoginResultDto.captchaFail("보안문자를 정확히 입력해 주세요.");
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
        // 실제 로그인 서비스 호출 (ID/PW + 잠금 + 정책)
        // ==========================
        EmpLoginResultDto result = empAcctService.login(vendId, loginId, password);
	        
	    // ✅ 구독 결제 필요(마스터만 들어오는 케이스): 세션 생성 후 구독 페이지로 보내기
	       if (!result.isSuccess()
	               && result.isSubscriptionRequired()
	               && result.getEmpAcct() != null) {
	
	            EmpAcctVO empAcct = result.getEmpAcct();
	
	            // 세션 생성
	            SessionDto loginEmp = loginSessionBuilder.build(empAcct);
	
	            // ★ 핵심: 정상계정(r1)이어도 "세션에서는 r4로" 만들어서 인터셉터가 구독플로우로 제한하게 함
	            loginEmp.setAcctSt("r4");
	
	            session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);
	            applySessionPolicy(session, loginEmp.getVendId());
	
	            // 프론트는 success=true일 때 redirect 하니까 success=true로 재포장
	            EmpLoginResultDto ok = EmpLoginResultDto.ok(empAcct);
	            ok.setSubscriptionRequired(true);
	            ok.setMessage(result.getMessage());
	            ok.setRedirectUrl("/SubscriptionChoice");
	            return ok;
	        }

        // ---------------------------------
        // 최종 로그인 성공 (OTP 미사용)
        // ---------------------------------
        if (result.isSuccess() && result.getEmpAcct() != null) {
            EmpAcctVO empAcct = result.getEmpAcct();

            SessionDto loginEmp = loginSessionBuilder.build(empAcct);
            session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);
            
            applySessionPolicy(session, loginEmp.getVendId());
            
            log.info(">>> 로그인 + 세션 저장 완료: sessionId={}, empAcctId={}, empNm={}, deptNm={}, deptId={},"
            		+ " empId={}, loginId={}, vendId={}, masYn={}, bizcnd={}, addr={}, cttpc={}, hp={},"
            		+ " AuthCodes={}, RoleIds = {}, ProofPhoto = {}",
                    session.getId(),
                    loginEmp.getEmpAcctId(), loginEmp.getEmpNm(),
                    loginEmp.getDeptNm(), loginEmp.getDeptId(),
                    loginEmp.getEmpId(), loginEmp.getLoginId(),
                    loginEmp.getVendId(), loginEmp.getMasYn(),
                    loginEmp.getBizcnd(), loginEmp.getAddr(),
                    loginEmp.getCttpc(), loginEmp.getHp(),
                    loginEmp.getAuthCodes(), loginEmp.getRoleIds(), loginEmp.getProofPhoto());

            return result; // success=true, otpRequired=false
        }

        // ---------------------------------
        // OTP가 필요한 상태 (ID/PW OK + OTP_YN = Y)
        //    => success=false, otpRequired=true, empAcct != null
        // ---------------------------------
        if (!result.isSuccess() && result.isOtpRequired() && result.getEmpAcct() != null) {
            EmpAcctVO empAcct = result.getEmpAcct();

            // OTP 정책 조회 (otp_valid_min, otp_fail_cnt)
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

            // OTP 대기 중인 계정 정보 임시 저장
            session.setAttribute(SessionConst.PENDING_LOGIN_EMP, empAcct);

            // OTP 코드 생성 + 만료시간/실패횟수 제한 저장
            String otpCode = generateOtpCode(6);
            long now = System.currentTimeMillis();
            long expireTimeMillis = now + (otpValidMin * 60L * 1000L); // 정책 기준 유효 시간

            session.setAttribute(SessionConst.LOGIN_OTP_CODE, otpCode);
            session.setAttribute(SessionConst.LOGIN_OTP_EXPIRE, expireTimeMillis);
            session.setAttribute(SessionConst.LOGIN_OTP_FAIL_CNT, 0);              // 현재 실패 횟수
            session.setAttribute(SessionConst.LOGIN_OTP_FAIL_LIMIT, otpFailLimit); // 허용 실패 횟수

            // OTP 문자 발송 (hp → cttpc 순으로 사용)
            String targetMobile = selectOtpTargetNumber(empAcct); // hp 우선, 없으면 cttpc
            if (targetMobile != null && !targetMobile.isBlank()) {
                // 정재민 아래 기능은 문자 발송 기능
                smsService.sendOtpSms(targetMobile, otpCode, otpValidMin);
                
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
        // 일반 실패 (비밀번호 틀림/잠금/기타 사유)
        // ---------------------------------
        return result;
    }

    // ==========================
    // 2) OTP 검증 API (2단계 로그인)
    // ==========================
    @PostMapping("/logIn/otp")
    public EmpLoginResultDto verifyOtp(@RequestParam("otpCode") String otpCode,
                                       HttpSession session) {
        
        String savedOtp      = (String) session.getAttribute(SessionConst.LOGIN_OTP_CODE);
        Long expireMillis    = (Long) session.getAttribute(SessionConst.LOGIN_OTP_EXPIRE);
        EmpAcctVO pendingEmp = (EmpAcctVO) session.getAttribute(SessionConst.PENDING_LOGIN_EMP);

        Integer failCntObj   = (Integer) session.getAttribute(SessionConst.LOGIN_OTP_FAIL_CNT);
        Integer failLimitObj = (Integer) session.getAttribute(SessionConst.LOGIN_OTP_FAIL_LIMIT);

        int failCnt   = (failCntObj == null ? 0 : failCntObj);
        int failLimit = (failLimitObj == null || failLimitObj <= 0 ? DEFAULT_OTP_FAIL_LIMIT : failLimitObj);

        // OTP 세션정보가 없는 경우 (직접 URL 접근 / 세션 만료 등)
        if (savedOtp == null || expireMillis == null || pendingEmp == null) {
            return EmpLoginResultDto.fail("OTP 세션 정보가 없습니다. 다시 로그인해 주세요.");
        }

        // 만료 시간 체크 (otp_valid_min 기준)
        long now = System.currentTimeMillis();
        if (now > expireMillis) {
            // 만료된 OTP 정보는 지우고 재로그인 유도
            clearOtpSession(session);
            return EmpLoginResultDto.fail("OTP 유효 시간이 지났습니다. 다시 로그인해 주세요.");
        }

        // 코드 일치 여부 확인
        if (otpCode == null || !savedOtp.equals(otpCode.trim())) {
            failCnt++;
            session.setAttribute(SessionConst.LOGIN_OTP_FAIL_CNT, failCnt);

            if (failCnt >= failLimit) {
                // 이 OTP 세션 전체를 "로그인 실패 1회"로 처리
                empAcctService.increaseLoginFailByOtp(pendingEmp);

                //  OTP 관련 세션 정리
                clearOtpSession(session);

                // 다시 로그인 유도
                return EmpLoginResultDto.fail(
                        "OTP를 " + failLimit + "회 이상 잘못 입력하여 다시 로그인해 주세요."
                );
            } else {
                int remain = failLimit - failCnt;
                return EmpLoginResultDto.fail(
                        "OTP 코드가 올바르지 않습니다. (남은 시도 횟수: " + remain + "회)"
                );
            }
        }

        // OTP 검증 성공 → 최종 로그인 세션 생성
        EmpAcctVO empAcct = pendingEmp;

        SessionDto loginEmp = loginSessionBuilder.build(empAcct);
        session.setAttribute(SessionConst.LOGIN_EMP, loginEmp);
        
        applySessionPolicy(session, loginEmp.getVendId());

        log.info(">>> OTP 로그인 + 세션 저장 완료: sessionId={}, empAcctId={}, empNm={}, deptNm={}, deptId={}, empId={}, loginId={},"
        		+ " vendId={}, masYn={}, bizcnd={}, addr={}, cttpc={}, hp={}, AuthCodes={}, RoleIds ={}"
        		+ " roleId={}, proofPhoto={}",
                session.getId(),
                loginEmp.getEmpAcctId(), loginEmp.getEmpNm(),
                loginEmp.getDeptNm(), loginEmp.getDeptId(),
                loginEmp.getEmpId(), loginEmp.getLoginId(),
                loginEmp.getVendId(), loginEmp.getMasYn(),
                loginEmp.getBizcnd(), loginEmp.getAddr(),
                loginEmp.getCttpc(), loginEmp.getHp(),
                loginEmp.getAuthCodes(), loginEmp.getRoleIds(),

                loginEmp.getRoleId(), loginEmp.getProofPhoto());



        // OTP 관련 임시 세션은 제거
        clearOtpSession(session);

        // 최종 성공 응답
        return EmpLoginResultDto.ok(empAcct);
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
	/*
	 * private SessionDto buildSessionEmp(EmpAcctVO empAcct) { SessionDto loginEmp =
	 * new SessionDto(); loginEmp.setEmpAcctId(empAcct.getEmpAcctId());
	 * loginEmp.setVendId(empAcct.getVendId());
	 * loginEmp.setEmpId(empAcct.getEmpId());
	 * loginEmp.setLoginId(empAcct.getLoginId());
	 * loginEmp.setEmpNm(empAcct.getEmpNm());
	 * loginEmp.setDeptId(empAcct.getDeptId());
	 * loginEmp.setDeptNm(empAcct.getDeptNm());
	 * loginEmp.setMasYn(empAcct.getMasYn());
	 * loginEmp.setBizcnd(empAcct.getBizcnd()); loginEmp.setAddr(empAcct.getAddr());
	 * loginEmp.setCttpc(empAcct.getCttpc()); loginEmp.setHp(empAcct.getHp());
	 * loginEmp.setTempYn(empAcct.getTempYn());
	 * loginEmp.setRoleIds(empAcct.getRoleIds());
	 * loginEmp.setAuthCodes(empAcct.getAuthCodes());
	 * loginEmp.setEmail(empAcct.getEmail());
	 * loginEmp.setProofPhoto(empAcct.getProofPhoto());
	 * loginEmp.setAcctSt(empAcct.getSt());
	 * 
	 * // 🔽 추가된 부분: masYn 기준으로 roleId 세팅 // - 예시: masYn == 'e1' 이면 HR 관리자 권한 String
	 * roleId = "ROLE_USER"; if ("e1".equals(empAcct.getMasYn())) { roleId =
	 * "ROLE_HR_ADMIN"; } loginEmp.setRoleId(roleId); // 🔼 여기까지만 새로 추가됨
	 * 
	 * 
	 * // empAcct.getEmpAcctId() = tb_emp_acct PK // empAcct.getVendId() = 회사 코드
	 * java.util.List<MenuAuthDto> menuAuthList =
	 * menuAuthMapper.selectMenuAuthByEmpAcct(empAcct.getEmpAcctId(),
	 * empAcct.getVendId());
	 * 
	 * java.util.Map<String, MenuAuthDto> menuAuthMap = new java.util.HashMap<>();
	 * for (MenuAuthDto dto : menuAuthList) { // key 는 menuId 기준으로 사용 (예: "HR_ATTD",
	 * "HR_VCATN"...) menuAuthMap.put(dto.getMenuId(), dto); }
	 * loginEmp.setMenuAuthMap(menuAuthMap);
	 * 
	 * log.info(">>> 메뉴 권한 로드: empAcctId={}, size={}", empAcct.getEmpAcctId(),
	 * loginEmp.getMenuAuthMap() != null ? loginEmp.getMenuAuthMap().size() : 0);
	 * 
	 * return loginEmp;
	 * 
	 * 
	 * }
	 */

    // ==========================
    // OTP 문자 발송 대상 번호 선택 (cttpc)
    // ==========================
    private String selectOtpTargetNumber(EmpAcctVO empAcct) {
        if (empAcct == null) return null;

        // cttpc(사원 연락처)만 사용
        String cttpc = empAcct.getCttpc();
        if (cttpc != null && !cttpc.isBlank()) {
            return cttpc;
        }
        return null;
    }
    
    // ==========================
    // OTP 재발급 API
    // ==========================
    @PostMapping("/logIn/otp/resend")
    public EmpLoginResultDto resendOtp(HttpSession session) {
    
        EmpAcctVO pendingEmp = (EmpAcctVO) session.getAttribute(SessionConst.PENDING_LOGIN_EMP);
        if (pendingEmp == null) {
            return EmpLoginResultDto.fail("OTP 세션 정보가 없습니다. 다시 로그인해 주세요.");
        }
    
        Integer failCntObj   = (Integer) session.getAttribute(SessionConst.LOGIN_OTP_FAIL_CNT);
        Integer failLimitObj = (Integer) session.getAttribute(SessionConst.LOGIN_OTP_FAIL_LIMIT);
    
        int failCnt   = (failCntObj == null ? 0 : failCntObj);
        int failLimit = (failLimitObj == null || failLimitObj <= 0 ? DEFAULT_OTP_FAIL_LIMIT : failLimitObj);
    
        // 이미 OTP 실패 한도를 넘은 경우 → 재로그인 유도
        if (failCnt >= failLimit) {
            clearOtpSession(session);
            return EmpLoginResultDto.fail("OTP 재발급 가능 횟수를 초과했습니다. 다시 로그인해 주세요.");
        }
    
        // OTP 정책 재조회 (유효 시간만 쓰면 됨)
        SecPolicyVO policy = empAcctService.getSecPolicy(pendingEmp.getVendId());
    
        int otpValidMin = DEFAULT_OTP_VALID_MIN;
        if (policy != null && policy.getOtpValidMin() != null && policy.getOtpValidMin() > 0) {
            otpValidMin = policy.getOtpValidMin();
        }
    
        // 새 OTP 생성 + 만료시간 갱신
        String otpCode = generateOtpCode(6);
        long now = System.currentTimeMillis();
        long expireTimeMillis = now + (otpValidMin * 60L * 1000L);
    
        session.setAttribute(SessionConst.LOGIN_OTP_CODE, otpCode);
        session.setAttribute(SessionConst.LOGIN_OTP_EXPIRE, expireTimeMillis);
    
        String targetMobile = selectOtpTargetNumber(pendingEmp);
        if (targetMobile != null && !targetMobile.isBlank()) {
            // 정재민 나중에 주석으로 막을 예정
            smsService.sendOtpSms(targetMobile, otpCode, otpValidMin);
    
            log.info(">>> [DEV ONLY] OTP 재발급: to={}, otpCode={}, validMin={}",
                    targetMobile, otpCode, otpValidMin);
        } else {
            log.warn("OTP 재발급 문자 발송 불가 - hp/cttpc 모두 없음: empAcctId={}", pendingEmp.getEmpAcctId());
            log.info(">>> [DEV ONLY] OTP 재발급 (문자 미발송, 번호 없음): vendId={}, loginId={}, otpCode={}, validMin={}",
                    pendingEmp.getVendId(), pendingEmp.getLoginId(), otpCode, otpValidMin);
        }
    
        // 프론트에서는 OTP 입력 박스 그대로 두고 메시지만 띄우면 되므로
        return EmpLoginResultDto.otpStep(pendingEmp, "새 OTP를 전송했습니다.");
    }
     
    // ==========================
    // 세션 타임아웃 정책 적용 유틸
    // ==========================
    private void applySessionPolicy(HttpSession session, String vendId) {

        // 거래처별(또는 default) 보안 정책 조회
        SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(vendId);

        Integer timeoutMin = policy.getSessionTimeoutMin();
        if (timeoutMin == null || timeoutMin <= 0) {
            timeoutMin = 30; // 안전장치 (VO 기본값과 맞춤)
        }

        // 실제 세션 타임아웃 설정 (초 단위)
        session.setMaxInactiveInterval(timeoutMin * 60);

        // 나중에 세션 연장 API에서 재사용할 수 있도록 세션에 정책 저장
        session.setAttribute(SessionConst.SESSION_TIMEOUT_MIN, timeoutMin);
        session.setAttribute(SessionConst.SESSION_TIMEOUT_ACTION, policy.getTimeoutAction());

        log.info("세션 정책 적용: vendId={}, timeoutMin={}, timeoutAction={}",
                vendId, timeoutMin, policy.getTimeoutAction());
    }
    
    // ==========================
    // 로그아웃
    // ==========================
    @PostMapping("/logIn/logout")
    public EmpLoginResultDto logout(HttpSession session) {
        session.invalidate();
        return EmpLoginResultDto.ok();
    }

}
