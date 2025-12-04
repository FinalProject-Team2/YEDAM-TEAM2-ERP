package store.yd2team.common.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.mapper.EmpAcctMapper;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.service.EmpAcctService;
import store.yd2team.common.service.EmpAcctVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpAcctServiceImpl implements EmpAcctService{

	private final EmpAcctMapper empAcctMapper;
	private final EmpLoginMapper empLoginMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkPassword(String vendId, String loginId, String rawPassword) {

        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            log.warn("checkPassword - 계정 없음: vendId={}, loginId={}", vendId, loginId);
            return false;
        }

        String dbPwd = empAcct.getLoginPwd();
        if (dbPwd == null) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, dbPwd);
    }

    @Override
    @Transactional
    public void changePassword(String vendId, String loginId, String rawNewPassword) {

        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            throw new IllegalArgumentException("계정을 찾을 수 없습니다.");
        }

        String encoded = passwordEncoder.encode(rawNewPassword);

        empAcctMapper.updatePassword(
                empAcct.getEmpAcctId(),
                encoded,
                empAcct.getEmpId()  // updt_by = empId
        );

        log.info(">>> 비밀번호 변경 완료: empAcctId={}, vendId={}, empId={}",
                empAcct.getEmpAcctId(), vendId, empAcct.getEmpId());
    }

    @Override
    @Transactional
    public void clearTempPasswordFlag(String vendId, String loginId) {

        EmpAcctVO empAcct = empLoginMapper.selectByLogin(vendId, loginId);
        if (empAcct == null) {
            log.warn("clearTempPasswordFlag - 계정 없음: vendId={}, loginId={}", vendId, loginId);
            return;
        }

        empAcctMapper.clearTempPasswordFlag(
                empAcct.getEmpAcctId(),
                empAcct.getEmpId() // updt_by = empId
        );

        log.info(">>> 임시 비밀번호 플래그 해제: empAcctId={}, vendId={}, empId={}",
                empAcct.getEmpAcctId(), vendId, empAcct.getEmpId());
    }

}
