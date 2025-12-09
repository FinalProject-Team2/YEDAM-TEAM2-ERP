package store.yd2team.common.service.impl;

import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.ACTIVE;
import static store.yd2team.common.consts.CodeConst.Yn.Y;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.EmpAcctEmployeeDto;
import store.yd2team.common.dto.EmpAcctSaveRequestDto;
import store.yd2team.common.dto.EmpAcctSaveResultDto;
import store.yd2team.common.dto.EmpDeptDto;
import store.yd2team.common.mapper.EmpAcctMapper;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.service.EmpAcctService;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.SmsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpAcctServiceImpl implements EmpAcctService{

	private final EmpAcctMapper empAcctMapper;
	private final EmpLoginMapper empLoginMapper;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

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
    
    @Override
    public List<EmpAcctEmployeeDto> searchEmployees(String vendId,
                                                    String deptName,
                                                    String jobName,
                                                    String empName,
                                                    String loginId) {

        log.debug("[EmpAcctMgmtService] searchEmployees vendId={}, deptName={}, jobName={}, empName={}, loginId={}",
                vendId, deptName, jobName, empName, loginId);

        return empAcctMapper.selectEmpEmployeeList(vendId,
									               deptName,
									               jobName,
									               empName,
									               loginId);
    }
    
    @Override
    public List<EmpDeptDto> findEmpDeptList(String vendId) {
        return empAcctMapper.selectEmpDeptList(vendId);
    }
    
    @Override
    public List<EmpAcctEmployeeDto> autocompleteEmpName(String vendId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return empAcctMapper.selectEmpNameAutoComplete(vendId, keyword);
    }

    // 🔹 계정 ID 자동완성
    @Override
    public List<EmpAcctEmployeeDto> autocompleteLoginId(String vendId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return empAcctMapper.selectLoginIdAutoComplete(vendId, keyword);
    }
    
    @Override
    @Transactional
    public EmpAcctSaveResultDto saveEmpAccount(EmpAcctSaveRequestDto req, String loginEmpId) {

        // 1) 기존 계정 조회
        EmpAcctVO acct = null;
        if (req.getEmpAcctId() != null && !req.getEmpAcctId().isBlank()) {
            acct = empAcctMapper.selectByEmpAcctId(req.getEmpAcctId());
        } else {
            acct = empAcctMapper.selectByVendAndEmp(req.getVendId(), req.getEmpId());
        }

        boolean isNew = (acct == null);
        String oldStatus = isNew ? null : acct.getSt();
        String newStatus = req.getAcctStatus();

        boolean smsSend = false;
        String tempPwPlain = null;

     // 2) 신규 계정 생성
        if (isNew) {
            acct = new EmpAcctVO();
            // empAcctId 는 MyBatis <selectKey> 에서 BEFORE로 생성됨

            acct.setVendId(req.getVendId());
            acct.setEmpId(req.getEmpId());
            acct.setLoginId(req.getLoginId());
            acct.setSt(newStatus);
            acct.setFailCnt(0);
            acct.setTempYn(Y);    // 임시 비밀번호(미변경) 상태
            acct.setYn(Y);        // 사용여부
            acct.setCreaBy(loginEmpId);
            acct.setUpdtBy(loginEmpId);
            // 필요하면 기본 마스터 여부도 세팅
            // acct.setMasYn("e2"); // 일반 계정 같은 식으로

            // 신규 + ACTIVE(r1) 인 경우만 임시 비밀번호 발급
            if (ACTIVE.equals(newStatus)) {
                tempPwPlain = generateTempPassword();
                acct.setLoginPwd(passwordEncoder.encode(tempPwPlain));
                smsSend = true;
            }

            empAcctMapper.insertEmpAcct(acct);
            // insert 이후 acct.getEmpAcctId() 에 selectKey 로 생성된 값이 들어있음
        }
        // 3) 기존 계정 수정
        else {
            acct.setLoginId(req.getLoginId());
            acct.setSt(newStatus);
            acct.setUpdtBy(loginEmpId);

            // 기존 상태 != ACTIVE → ACTIVE 로 변경되는 경우
            if (!ACTIVE.equals(oldStatus) && ACTIVE.equals(newStatus)) {
                tempPwPlain = generateTempPassword();
                acct.setLoginPwd(passwordEncoder.encode(tempPwPlain));
                acct.setTempYn(Y);   // 임시 비밀번호 상태
                smsSend = true;
            }

            // INACTIVE / LOCKED / 기타 → 비밀번호 변경 없이 상태만 저장
            empAcctMapper.updateEmpAcct(acct);
        }

        // 4) 문자 발송
        if (smsSend && tempPwPlain != null) {
            // 사원 연락처 조회
            String phone = empAcctMapper.selectEmpPhone(req.getVendId(), req.getEmpId());

            if (phone != null && !phone.isBlank()) {
                try {
                    // SmsService 에 임시 비밀번호 발송용 메서드가 있다고 가정
                    // (sendTempPasswordSms(to, loginId, tempPassword) 형태)
                    smsService.sendTempPasswordSms(phone, req.getVendId(), req.getLoginId(), tempPwPlain);
                } catch (Exception e) {
                    log.error("임시 비밀번호 문자 발송 실패: vendId={}, empId={}, err={}",
                            req.getVendId(), req.getEmpId(), e.getMessage(), e);
                }
            } else {
                log.warn("임시 비밀번호 문자 발송 실패: 연락처 없음 (vendId={}, empId={})",
                        req.getVendId(), req.getEmpId());
            }
        }

        EmpAcctSaveResultDto result = new EmpAcctSaveResultDto();
        result.setSuccess(true);
        result.setSmsSent(smsSend);
        result.setAcctStatus(newStatus);
        return result;
    }

    // ==========================
    // 임시 비밀번호 생성 유틸 (8자리 영문+숫자)
    // ==========================
    private String generateTempPassword() {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

}
