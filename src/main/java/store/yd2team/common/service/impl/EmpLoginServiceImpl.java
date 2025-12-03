package store.yd2team.common.service.impl;

// 상태 코드 상수 import
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.ACTIVE;
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.LOCKED;
import static store.yd2team.common.consts.CodeConst.Yn.Y;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.EmpLoginResultDto;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.EmpLoginService;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpLoginServiceImpl implements EmpLoginService {

    private final EmpLoginMapper empLoginMapper;
    private final SecPolicyService secPolicyService;
    private final PasswordEncoder passwordEncoder;

    // 보안 정책이 없을 때 기본 최대 실패 횟수
    private static final int DEFAULT_MAX_FAIL_CNT = 5;

    @Override
    public EmpLoginResultDto login(String vendId, String loginId, String password) {

        // 계정 조회
        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            // 이때는 실패 횟수를 관리할 필요가 없으니 그냥 캡챠/OTP 없이 실패만 반환
            return EmpLoginResultDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 보안 정책 조회 (없으면 기본값 사용)
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

        log.info(">>> 로그인 잠금 체크: st={}, lockDttm={}, autoUnlockTm={}",
                empAcct.getSt(), empAcct.getLockDttm(), autoUnlockTm);

        // 잠금 상태인지 확인 + 자동 잠금 해제
        if (LOCKED.equals(empAcct.getSt())) {

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
                empAcct.setSt(ACTIVE);
                empAcct.setFailCnt(0);
            } else {
                long remain = autoUnlockTm - minutes;
                return EmpLoginResultDto.fail("잠금된 계정입니다. 약 " + remain + "분 후 다시 시도해주세요.");
            }
        }

        // 비밀번호 검증
        String dbPwd = empAcct.getLoginPwd();
        boolean passwordOk = (dbPwd != null && passwordEncoder.matches(password, dbPwd));

        // ===========================
        // 비밀번호 불일치 → 실패 처리 + 캡챠 정책
        // ===========================
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
                    // nextFailCnt 이상이 되는 순간부터 다음 시도에 캡챠 ON
                    captchaRequiredNext = true;
                }
            }

            if (nextFailCnt >= maxFailCnt) {
                // 이번 실패로 인해 잠금 조건 도달
                return EmpLoginResultDto.fail(
                        "비밀번호를 " + maxFailCnt + "회 이상 잘못 입력하여 계정이 잠겼습니다.",
                        captchaRequiredNext,
                        false  // OTP는 비밀번호가 맞아야 의미 있으므로 false
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

        // ===========================
        // 비밀번호 일치 → OTP 정책에 따라 분기
        // ===========================

        // 비밀번호는 맞았으므로 실패 카운트/잠금 관련 필드는 초기화
        empLoginMapper.updateLoginSuccess(empAcct.getEmpAcctId(), empAcct.getEmpId());

        boolean otpEnabled = false;
        if (policy != null && Y.equals(policy.getOtpYn())) {
            otpEnabled = true;
        }

        if (otpEnabled) {
            // OTP 사용: 1차(ID/PW) 성공 상태 → OTP 추가 인증 필요
            // success=false, otpRequired=true 상태로 반환
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
