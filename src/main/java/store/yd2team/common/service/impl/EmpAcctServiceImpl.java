package store.yd2team.common.service.impl;

// ★ 상태 코드 상수 import
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.ACTIVE;
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.LOCKED;
import static store.yd2team.common.consts.CodeConst.Yn.Y;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.LoginResultDto;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.mapper.SecPolicyMapper;
import store.yd2team.common.service.EmpAcctService;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.SecPolicyVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpAcctServiceImpl implements EmpAcctService {

    private final EmpLoginMapper empLoginMapper;
    private final SecPolicyMapper secPolicyMapper;
//    private final PasswordEncoder passwordEncoder;

    // 보안 정책이 없을 때 기본 최대 실패 횟수
    private static final int DEFAULT_MAX_FAIL_CNT = 5;

    @Override
    public LoginResultDto login(String vendId, String loginId, String password) {

        // 1) 계정 조회
        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            // 이때는 실패 횟수를 관리할 필요가 없으니 그냥 캡챠/OTP 없이 실패만 반환
            return LoginResultDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 2) 보안 정책 조회 (없으면 기본값 사용)
        SecPolicyVO policy = secPolicyMapper.selectByVendId(vendId);

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
        if (LOCKED.equals(empAcct.getSt())) {

            // 자동 잠금 해제 시간이 없으면 계속 잠금 상태 유지
            if (autoUnlockTm == null || autoUnlockTm <= 0) {
                return LoginResultDto.fail("잠금된 계정입니다. 관리자에게 문의하세요.");
            }

            LocalDateTime lockedAt = empAcct.getLockDttm();

            if (lockedAt == null) {
                return LoginResultDto.fail("잠금된 계정입니다. 관리자에게 문의하세요.");
            }

            long minutes = Duration.between(lockedAt, LocalDateTime.now()).toMinutes();

            if (minutes >= autoUnlockTm) {
                // 자동 해제
                empLoginMapper.unlock(empAcct.getEmpAcctId(), "SYSTEM");
                empAcct.setSt(ACTIVE);
                empAcct.setFailCnt(0);
            } else {
                long remain = autoUnlockTm - minutes;
                return LoginResultDto.fail("잠금된 계정입니다. 약 " + remain + "분 후 다시 시도해주세요.");
            }
        }

        // 4) 비밀번호 검증 (※ 추후 해시 적용 예정)
        String dbPwd = empAcct.getLoginPwd();
        boolean passwordOk = (dbPwd != null && dbPwd.equals(password));

        // ===========================
        // 4-1) 비밀번호 불일치 → 실패 처리 + 캡챠 정책
        // ===========================
        if (!passwordOk) {
            // 실패 횟수 + 잠금 처리 (DB 업데이트)
            empLoginMapper.updateLoginFail(empAcct.getEmpAcctId(), maxFailCnt, loginId);

            int currentFailCnt = (empAcct.getFailCnt() == null ? 0 : empAcct.getFailCnt());
            int nextFailCnt = currentFailCnt + 1; // 이번 실패 포함 카운트

            // ★ 여기서 “다음 로그인 시도부터 캡챠를 보여줘야 하는지” 판단
            boolean captchaRequiredNext = false;

            if (policy != null && Y.equals(policy.getCaptchaYn())) { // CAPTCHA_YN = 'Y'
                Integer captchaFailCnt = policy.getCaptchaFailCnt();
                if (captchaFailCnt != null && captchaFailCnt > 0 && nextFailCnt >= captchaFailCnt) {
                    // 예: captchaFailCnt = 3
                    // → nextFailCnt가 3 이상이 되는 순간부터 "다음 시도"에 캡챠 ON
                    captchaRequiredNext = true;
                }
            }

            if (nextFailCnt >= maxFailCnt) {
                // 이번 실패로 인해 잠금 조건 도달
                return LoginResultDto.fail(
                        "비밀번호를 " + maxFailCnt + "회 이상 잘못 입력하여 계정이 잠겼습니다.",
                        captchaRequiredNext,
                        false  // OTP는 비밀번호가 맞아야 의미 있으므로 false
                );
            } else {
                int remain = maxFailCnt - nextFailCnt;
                return LoginResultDto.fail(
                        "아이디 또는 비밀번호가 올바르지 않습니다. (남은 시도 횟수: " + remain + "회)",
                        captchaRequiredNext,
                        false
                );
            }
        }

        // ===========================
        // 4-2) 비밀번호 일치 → OTP 정책에 따라 분기
        // ===========================

        // 비밀번호는 맞았으므로 실패 카운트/잠금 관련 필드는 초기화
        // (비밀번호 기준 실패 횟수는 여기서 리셋해도 됨)
        empLoginMapper.updateLoginSuccess(empAcct.getEmpAcctId(), loginId);

        boolean otpEnabled = false;
        if (policy != null && Y.equals(policy.getOtpYn())) {
            otpEnabled = true;
        }

        if (otpEnabled) {
            // ★ OTP 사용: 1차(ID/PW) 성공 상태 → OTP 추가 인증 필요
            // success=false, otpRequired=true 상태로 반환
            return LoginResultDto.otpStep(empAcct, "OTP 인증이 필요합니다.");
        }

        // OTP 미사용 → 바로 로그인 최종 성공
        return LoginResultDto.ok(empAcct);
    }

    @Override
    public boolean isCaptchaRequired(String vendId, String loginId) {

        // 1) 계정 조회
        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            // 계정 자체가 없을 때부터 캡챠를 강제할지 말지는 정책 문제인데
            // 일단은 false로 두자. (원하면 나중에 true로 바꿔도 됨)
            return false;
        }

        // 2) 보안 정책 조회
        SecPolicyVO policy = secPolicyMapper.selectByVendId(vendId);
        if (policy == null) {
            return false;
        }

        // 3) 캡챠 사용 여부 (CAPTCHA_YN)
        if (!Y.equals(policy.getCaptchaYn())) { // Y면 사용, N이면 미사용
            return false;
        }

        // 4) 몇 번 틀린 후부터 캡챠?
        Integer threshold = policy.getCaptchaFailCnt();
        if (threshold == null || threshold <= 0) {
            return false;
        }

        // 현재 계정의 실패 횟수
        int failCnt = (empAcct.getFailCnt() == null) ? 0 : empAcct.getFailCnt();

        // "CAPTCHA_FAIL_CNT 회 이상 틀린 이후부터" → 그 다음 로그인 시도부터 캡챠 강제
        return failCnt >= threshold;
    }
    
    @Override
    public SecPolicyVO getSecPolicy(String vendId) {
        return secPolicyMapper.selectByVendId(vendId);
    }

}
