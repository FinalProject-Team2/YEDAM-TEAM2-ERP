package store.yd2team.common.service.impl;

// 상태 코드 상수 import
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.ACTIVE;
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.LOCKED;
import static store.yd2team.common.consts.CodeConst.Yn.Y;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.aop.SysLog;
import store.yd2team.common.aop.SysLogConfig;
import store.yd2team.common.dto.EmpLoginResultDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.mapper.SubscriptionMapper;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.EmpLoginService;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;
import store.yd2team.common.service.SystemLogService;

@Slf4j
@Service
@RequiredArgsConstructor
@SysLogConfig(module = "d2", table = "TB_EMP_ACCT", pkParam = "empAcctId")
public class EmpLoginServiceImpl implements EmpLoginService {

    private final EmpLoginMapper empLoginMapper;
    private final SecPolicyService secPolicyService;
    private final PasswordEncoder passwordEncoder;
    private final SystemLogService systemLogService;
    private final SubscriptionMapper subscriptionMapper;
    
    private SessionDto buildPseudoSession(EmpAcctVO acct, String actorEmpId) {
        SessionDto s = new SessionDto();
        s.setVendId(acct.getVendId());
        s.setEmpAcctId(acct.getEmpAcctId());
        s.setEmpId(actorEmpId);          // 누가 발생시켰는지(자동해제면 SYSTEM)
        s.setLoginId(acct.getLoginId());
        s.setEmpNm(acct.getEmpNm());     // 있으면
        return s;
    }

    private void writeSysLog(EmpAcctVO acct, String action, String summary, String actorEmpId) {
        SessionDto pseudo = buildPseudoSession(acct, actorEmpId);
        systemLogService.writeLog(
            pseudo,
            "d2",              // module
            action,            // lg3/lg4
            "TB_EMP_ACCT",      // table
            acct.getEmpAcctId(),// pk
            summary
        );
    }

    // 보안 정책이 없을 때 기본 최대 실패 횟수
    private static final int DEFAULT_MAX_FAIL_CNT = 5;

    @SysLog(
    		  action = "lg2",
    		  msg = "로그인",
    		  pkFromSession = false,
    		  onOk = "lg1",
    		  onFail = "lg2",
    		  onOtpStep = "lg6"
    		)
    @Override
    public EmpLoginResultDto login(String vendId, String loginId, String password) {


        // 1) 계정 조회
        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
        	
        	EmpAcctVO oprtr = empLoginMapper.selectOprtrByLogin(loginId);
            if (oprtr == null) {
                return EmpLoginResultDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
            }

            // 비번 검증
            if (!passwordEncoder.matches(password, oprtr.getLoginPwd())) {
                return EmpLoginResultDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
            }

            // 운영자 로그인 성공 처리 (구독/OTP/잠금정책은 운영자에겐 보통 스킵)
            return EmpLoginResultDto.ok(oprtr);
        }

        // 2) 보안 정책 조회 (없으면 기본값 사용)
        SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(vendId);

        log.info(">>> [LOGIN] 정책 확인: vendId={}, policyId={}, otpYn={}, otpValidMin={}, otpFailCnt={}",
                vendId,
                (policy == null ? null : policy.getPolicyId()),
                (policy == null ? null : policy.getOtpYn()),
                (policy == null ? null : policy.getOtpValidMin()),
                (policy == null ? null : policy.getOtpFailCnt()));

        int maxFailCnt = DEFAULT_MAX_FAIL_CNT;
        Integer autoUnlockTm = null;   // 단위: 분

        if (policy != null) {
            if (policy.getPwFailCnt() != null && policy.getPwFailCnt() > 0) {
                maxFailCnt = policy.getPwFailCnt();
            }
            autoUnlockTm = policy.getAutoUnlockTm();
        }

        log.info(">>> 로그인 잠금 체크: empAcctId={}, st={}, lockDttm={}, autoUnlockTm={}",
                empAcct.getEmpAcctId(), empAcct.getSt(), empAcct.getLockDttm(), autoUnlockTm);

        // 3) 잠금 상태인지 확인 + 자동 잠금 해제
        if (LOCKED.equals(empAcct.getSt())) {   // LOCKED == "r2" 라고 가정

            // 자동 잠금 해제 시간이 없으면 계속 잠금 상태 유지
            if (autoUnlockTm == null || autoUnlockTm <= 0) {
                return EmpLoginResultDto.fail("잠금된 계정입니다. 관리자에게 문의하세요.");
            }

            LocalDateTime lockedAt = empAcct.getLockDttm();
            if (lockedAt == null) {
                return EmpLoginResultDto.fail("잠금된 계정입니다. 관리자에게 문의하세요.");
            }

            long minutes = Duration.between(lockedAt, LocalDateTime.now()).toMinutes();

            if (minutes >= autoUnlockTm) {
                // 자동 해제
                empLoginMapper.unlock(empAcct.getEmpAcctId(), "SYSTEM");
                empAcct.setSt(ACTIVE);   // 상태를 정상(r1)으로 변경
                empAcct.setFailCnt(0);
                writeSysLog(empAcct, "lg4", "자동 잠금 해제", "SYSTEM");
            } else {
                long remain = autoUnlockTm - minutes;
                return EmpLoginResultDto.fail("잠금된 계정입니다. 약 " + remain + "분 후 다시 시도해주세요.");
            }
        }

        // 4) 상태(st) / 사용여부(yn) 공통 체크
        String st = empAcct.getSt();   // 자동 해지로 ACTIVE 로 바뀌었을 수도 있음
        String yn = empAcct.getYn();   // e1(사용), e2(중지 등)

        log.info(">>> [LOGIN] 상태/사용여부 체크: empAcctId={}, st={}, yn={}",
                empAcct.getEmpAcctId(), st, yn);

        // 4-1) 사용 여부(yn) 먼저 체크
        if ("e2".equals(yn)) {
            // 명시적으로 "사용 중지" 상태
            log.info(">>> [LOGIN BLOCK] 사용 중지 계정 로그인 시도: empAcctId={}, vendId={}, loginId={}",
                    empAcct.getEmpAcctId(), empAcct.getVendId(), empAcct.getLoginId());

            return EmpLoginResultDto.fail("사용 중지된 계정입니다.");
        } else if (!Y.equals(yn)) {
            // e2도 아니고, Y(e1)도 아니고, null/기타 코드인 경우
            log.warn(">>> [LOGIN BLOCK] 알 수 없는 사용여부(yn) 상태: empAcctId={}, yn={}",
                    empAcct.getEmpAcctId(), yn);

            return EmpLoginResultDto.fail("로그인할 수 없는 계정 상태입니다. 관리자에게 문의하세요.");
        }

        // 4-2) 상태(st) 체크 (ACTIVE가 아니면 로그인 불가)
        if (!ACTIVE.equals(st)) {

            if ("r4".equals(st)) {
                // 구독 해지: 마스터(masYn=e1)만 로그인 허용
                String masYn = empAcct.getMasYn(); // EmpAcctVO에 getter 있어야 함

                if (!"e1".equals(masYn)) {
                    // e2(일반) 또는 null 등은 전부 차단
                    return EmpLoginResultDto.fail("구독 해지된 회사입니다.");
                }

                // 마스터만 계속 진행
                log.info(">>> [LOGIN] 구독 해지 + 마스터 계정 로그인 허용: empAcctId={}, vendId={}",
                         empAcct.getEmpAcctId(), empAcct.getVendId());

            } else if ("r3".equals(st)) {
                return EmpLoginResultDto.fail("잠금 또는 비활성화된 계정입니다. 관리자에게 문의하세요.");
            } else {
                return EmpLoginResultDto.fail("로그인할 수 없는 계정 상태입니다. 관리자에게 문의하세요.");
            }
        }

        // ===========================
        // 5) 여기까지 온 계정만
        //    st = r1(ACTIVE), yn = e1 인 정상 계정
        // ===========================

        // 비밀번호 검증
        String dbPwd = empAcct.getLoginPwd();
        boolean passwordOk = (dbPwd != null && passwordEncoder.matches(password, dbPwd));

        if (!passwordOk) {
            // 실패 횟수 + 잠금 처리 (DB 업데이트)
            empLoginMapper.updateLoginFail(empAcct.getEmpAcctId(), maxFailCnt, empAcct.getEmpId());

            int currentFailCnt = (empAcct.getFailCnt() == null ? 0 : empAcct.getFailCnt());
            int nextFailCnt = currentFailCnt + 1; // 이번 실패 포함 카운트

            // 다음 로그인 시도부터 캡챠를 보여줘야 하는지 판단
            boolean captchaRequiredNext = false;

            if (policy != null && Y.equals(policy.getCaptchaYn())) { // CAPTCHA_YN = 'Y'
                Integer captchaFailCnt = policy.getCaptchaFailCnt();
                if (captchaFailCnt != null && captchaFailCnt > 0 && nextFailCnt >= captchaFailCnt) {
                    captchaRequiredNext = true;
                }
            }

            if (nextFailCnt >= maxFailCnt) {
            	
            	writeSysLog(empAcct, "lg3", "계정 잠금 발생(비밀번호 실패 횟수 초과)", "SYSTEM");
            	 
                return EmpLoginResultDto.fail(
                        "비밀번호를 " + maxFailCnt + "회 이상 잘못 입력하여 계정이 잠겼습니다.",
                        captchaRequiredNext,
                        false
                );
            } else {
                int remain = maxFailCnt - nextFailCnt;
                return EmpLoginResultDto.fail(
                        "아이디 또는 비밀번호가 올바르지 않습니다. (남은 시도 횟수: " + remain + "회)",
                        captchaRequiredNext,
                        false
                );
            }
        }

        // 비밀번호 일치 → 실패 카운트/잠금 관련 필드 초기화
        empLoginMapper.updateLoginSuccess(empAcct.getEmpAcctId(), empAcct.getEmpId());
        
        List<String> roleIds = empLoginMapper.selectRoleIdsByEmpAcctId(empAcct.getEmpAcctId());
        empAcct.setRoleIds(roleIds);

        Set<String> authCodes = new HashSet<>();
        // authCodes.addAll(empLoginMapper.selectAuthCodesByEmpAcctId(empAcct.getEmpAcctId()));
        empAcct.setAuthCodes(authCodes);
        
	     // ===========================
	     // 구독 활성 여부 체크 추가
	     // ===========================
	     boolean hasActiveSubsp = subscriptionMapper.countActiveSubsp(empAcct.getVendId()) > 0;
	
	     if (!hasActiveSubsp) {
	         // 활성 구독 없음
	         if ("e1".equals(empAcct.getMasYn())) {
	             // 마스터는 로그인은 시키되, r4 플로우로 보내기 위한 플래그
	             return EmpLoginResultDto.subscriptionRequired(empAcct, "구독 결제가 필요합니다.");
	         } else {
	             return EmpLoginResultDto.fail("구독 결제가 필요합니다. 관리자에게 문의하세요.");
	         }
	     }
	
	     boolean otpEnabled = false;
	     if (policy != null && Y.equals(policy.getOtpYn())) {
	         otpEnabled = true;
	     }
	
	     if (otpEnabled) {
	         return EmpLoginResultDto.otpStep(empAcct, "OTP 인증이 필요합니다.");
	     }
	
	        // OTP 미사용 → 바로 로그인 최종 성공
	        return EmpLoginResultDto.ok(empAcct);
	    }

    @Override
    public boolean isCaptchaRequired(String vendId, String loginId) {

        // 계정 조회
        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            // 계정 자체가 없을 때 안 함
            return false;
        }

        // 보안 정책 조회
        SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(vendId);
        if (policy == null) {
            return false;
        }

        // 캡챠 사용 여부 (CAPTCHA_YN)
        if (!Y.equals(policy.getCaptchaYn())) { // Y면 사용, N이면 미사용
            return false;
        }

        // 몇 번 틀린 후부터 캡챠?
        Integer threshold = policy.getCaptchaFailCnt();
        if (threshold == null || threshold <= 0) {
            return false;
        }

        // 현재 계정의 실패 횟수
        int failCnt = (empAcct.getFailCnt() == null) ? 0 : empAcct.getFailCnt();

        // CAPTCHA_FAIL_CNT 회 이상 틀린 이후부터 그 다음 로그인 시도부터 캡챠 강제
        return failCnt >= threshold;
    }
    
    @Override
    public SecPolicyVO getSecPolicy(String vendId) {
        return secPolicyService.getByVendIdOrDefault(vendId);
    }
    
    @SysLog(action = "lg5", msg = "OTP 실패", pkField = "empAcctId")
    @Override
    public void increaseLoginFailByOtp(EmpAcctVO empAcct) {
        if (empAcct == null) {
            return;
        }

        // 해당 계정이 속한 거래처의 보안 정책 조회
        String vendId = empAcct.getVendId();
        SecPolicyVO policy = secPolicyService.getByVendIdOrDefault(vendId);

        int maxFailCnt = DEFAULT_MAX_FAIL_CNT;
        if (policy != null && policy.getPwFailCnt() != null && policy.getPwFailCnt() > 0) {
            maxFailCnt = policy.getPwFailCnt();
        }

        // 기존 로그인 실패 처리 로직 재사용
        empLoginMapper.updateLoginFail(
                empAcct.getEmpAcctId(),
                maxFailCnt,
                empAcct.getEmpId()
        );

        log.info(">>> [OTP] OTP 실패 한도 도달 → 로그인 실패 1회 반영: empAcctId={}, vendId={}, empId={}, maxFailCnt={}",
                empAcct.getEmpAcctId(), vendId, empAcct.getEmpId(), maxFailCnt);
    }

}
