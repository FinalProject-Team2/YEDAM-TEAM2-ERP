package store.yd2team.common.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;

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

	private static final int DEFAULT_MAX_FAIL_CNT = 5;

	@Override
	public LoginResultDto login(String vendId, String loginId, String password) {

		// 1) 계정 조회
		EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
		if (empAcct == null) {
			return LoginResultDto.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
		}

		// 2) 보안 정책 조회 (없으면 기본값 사용)
		SecPolicyVO policy = secPolicyMapper.selectByVendId(vendId);
		
		int maxFailCnt = DEFAULT_MAX_FAIL_CNT;
		Integer autoUnlockTm = null;

		if (policy != null) {
			if (policy.getPwFailCnt() != null && policy.getPwFailCnt() > 0) {
				maxFailCnt = policy.getPwFailCnt();
			}
			autoUnlockTm = policy.getAutoUnlockTm();
		}
		
		log.info(">>> 로그인 잠금 체크: st={}, lockDttm={}, autoUnlockTm={}", empAcct.getSt(), empAcct.getLockDttm(),
				autoUnlockTm);

		// 3) 잠금 상태인지 확인 + 자동 잠금해제
		if ("LOCKED".equalsIgnoreCase(empAcct.getSt())) {

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
				empAcct.setSt("ACTIVE");
				empAcct.setFailCnt(0);
			} else {
				long remain = autoUnlockTm - minutes;
				return LoginResultDto.fail("잠금된 계정입니다. 약 " + remain + "분 후 다시 시도해주세요.");
			}
		}

		// 4) 비밀번호 검증
		// TODO: 나중에 BCryptPasswordEncoder로 교체
		String dbPwd = empAcct.getLoginPwd();
		boolean passwordOk = (dbPwd != null && dbPwd.equals(password));

		if (!passwordOk) {
            // 실패 횟수 + 잠금 처리
            empLoginMapper.updateLoginFail(empAcct.getEmpAcctId(), maxFailCnt, loginId);

            int currentFailCnt = (empAcct.getFailCnt() == null ? 0 : empAcct.getFailCnt());
            int nextFailCnt = currentFailCnt + 1; // 이번 실패까지 반영한 횟수

            if (nextFailCnt >= maxFailCnt) {
                // 이번 실패로 인해 잠금 조건에 도달
                return LoginResultDto.fail(
                        "비밀번호를 " + maxFailCnt + "회 이상 잘못 입력하여 계정이 잠겼습니다.");
            } else {
                int remain = maxFailCnt - nextFailCnt;
                return LoginResultDto.fail(
                        "아이디 또는 비밀번호가 올바르지 않습니다. (남은 시도 횟수: " + remain + "회)");
            }
        }

		// 5) 로그인 성공 처리
		empLoginMapper.updateLoginSuccess(empAcct.getEmpAcctId(), loginId);

		return LoginResultDto.ok(empAcct);
	}

}
