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

    	 // 1) 계정 조회
        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            // 계정이 존재하지 않을 때
            return EmpLoginResultDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
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

        log.info(">>> 로그인 잠금 체크: st={}, lockDttm={}, autoUnlockTm={}",
                empAcct.getSt(), empAcct.getLockDttm(), autoUnlockTm);

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
            } else {
                long remain = autoUnlockTm - minutes;
                return EmpLoginResultDto.fail("잠금된 계정입니다. 약 " + remain + "분 후 다시 시도해주세요.");
            }
        }

        // 4) 상태(st) / 사용여부(yn) 공통 체크
        String st = empAcct.getSt();     // 자동해지로 ACTIVE 로 바뀌었을 수도 있음
        String yn = empAcct.getYn();    // e1(사용), e2(중지)

        // yn = e2 → 사용 중지
        if (!Y.equals(yn)) { // CodeConst.Yn.Y == "e1"
            return EmpLoginResultDto.fail("사용 중지된 계정입니다.");
        }

        // st = ACTIVE 가 아닌 모든 상태는 로그인 불가
        if (!ACTIVE.equals(st)) {

            // r3 → 비활성 / 관리자 처리 대상
            if ("r3".equals(st)) {
                return EmpLoginResultDto.fail("잠금 또는 비활성화된 계정입니다. 관리자에게 문의하세요.");
            }

            // r4 → 구독 해지
            if ("r4".equals(st)) {
                return EmpLoginResultDto.fail("구독 해지 상태입니다.");
            }

            // 그 밖의 예외 상태
            return EmpLoginResultDto.fail("로그인할 수 없는 계정 상태입니다. 관리자에게 문의하세요.");
        }

        // ===========================
        // 5) 여기까지 온 계정만
        //    st = r1(ACTIVE), yn = e1 인 정상 로그인 가능 계정
        //    → 기존 비밀번호/캡챠/OTP 로직 그대로 수행
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

        boolean otpEnabled = false;
        if (policy != null && Y.equals(policy.getOtpYn())) {
            otpEnabled = true;
        }

        if (otpEnabled) {
            // OTP 사용: 1차(ID/PW) 성공 상태 → OTP 추가 인증 필요
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
