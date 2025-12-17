package store.yd2team.common.service.impl;

import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.ACTIVE;
import static store.yd2team.common.consts.CodeConst.Yn.Y;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.aop.SysLog;
import store.yd2team.common.aop.SysLogConfig;
import store.yd2team.common.dto.EmpAcctEmployeeDto;
import store.yd2team.common.dto.EmpAcctRoleDto;
import store.yd2team.common.dto.EmpAcctSaveRequestDto;
import store.yd2team.common.dto.EmpAcctSaveResultDto;
import store.yd2team.common.dto.EmpDeptDto;
import store.yd2team.common.mapper.EmpAcctMapper;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.service.EmpAcctService;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.common.service.SmsService;


@SysLogConfig(module = "d2", table = "TB_EMP_ACCT", pkParam = "empAcctId")
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
    @SysLog(action = "ac1", msg = "비밀번호 변경", pkFromSession = true, pkField = "empAcctId")
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

        // ✅ 결과 객체는 위에서 미리 만들어 두고, 실패 시에도 여기로 리턴
        EmpAcctSaveResultDto result = new EmpAcctSaveResultDto();

        try {
            // 1) 기존 계정 조회 (기존 로직 그대로)
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

            // 2) 신규 계정 생성 (기존 로직 그대로)
            if (isNew) {
                acct = new EmpAcctVO();

                acct.setVendId(req.getVendId());
                acct.setEmpId(req.getEmpId());
                acct.setLoginId(req.getLoginId());
                acct.setSt(newStatus);
                acct.setFailCnt(0);
                acct.setTempYn(Y);
                acct.setYn(Y);
                acct.setCreaBy(loginEmpId);
                acct.setUpdtBy(loginEmpId);

                if (ACTIVE.equals(newStatus)) {
                    tempPwPlain = generateTempPassword();
                    acct.setLoginPwd(passwordEncoder.encode(tempPwPlain));
                    smsSend = true;
                }

                empAcctMapper.insertEmpAcct(acct); // ✅ 여기서 UQ_EMP_ACCT_VEND_LOGIN 중복이면 예외 발생
            }
            // 3) 기존 계정 수정 (기존 로직 그대로)
            else {
                if (req.getLoginId() != null && !req.getLoginId().isBlank()) {
                    acct.setLoginId(req.getLoginId());
                }

                acct.setSt(newStatus);
                acct.setUpdtBy(loginEmpId);

                if (!ACTIVE.equals(oldStatus) && ACTIVE.equals(newStatus)) {
                    tempPwPlain = generateTempPassword();
                    acct.setLoginPwd(passwordEncoder.encode(tempPwPlain));
                    acct.setTempYn(Y);
                    smsSend = true;
                }

                empAcctMapper.updateEmpAcct(acct); // ✅ loginId 변경 시에도 여기서 중복 가능
            }

            // 4) 문자 발송 (✅ 기존 로직 유지 - 지우지 말고 그대로 둠)
            if (smsSend && tempPwPlain != null) {
                String phone = empAcctMapper.selectEmpPhone(req.getVendId(), req.getEmpId());

                if (phone != null && !phone.isBlank()) {
                    try {
                        // ✅ 나중에 주석 해제해서 보낼 거면 그대로 두면 됨
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

            // 5) 역할 매핑 저장 (기존 로직 그대로)
            String empAcctId = acct.getEmpAcctId();
            String vendId    = acct.getVendId();

            if (empAcctId != null) {
                List<String> roleIds = req.getRoleIds();

                if (roleIds != null) {
                    empAcctMapper.deleteEmpRoles(empAcctId);

                    if (!roleIds.isEmpty()) {
                        for (String roleId : roleIds) {
                            empAcctMapper.insertEmpRole(empAcctId, roleId, vendId, loginEmpId);
                        }
                    }
                }
            }

            // ✅ 성공 응답
            result.setSuccess(true);
            result.setSmsSent(smsSend);
            result.setAcctStatus(newStatus);
            result.setEmpAcctId(acct.getEmpAcctId());
            return result;

        } catch (DuplicateKeyException e) {

            // ✅ 여기서 “어떤 UNIQUE냐”를 제약조건명으로 구분
            String constraint = extractOracleConstraintName(e);

            if ("UQ_EMP_ACCT_VEND_LOGIN".equalsIgnoreCase(constraint)) {
                // (vend_id, login_id) 중복만 이 메시지
                result.setSuccess(false);
                result.setSmsSent(false);
                result.setAcctStatus(req.getAcctStatus());
                // DTO에 아래 필드가 있으면 세팅(없으면 success=false만 내려도 됨)
                result.setErrorCode("DUP_LOGIN_ID");
                result.setErrorMessage("중복된 아이디입니다. 다른 아이디를 입력해주세요.");
                return result;
            }

            // ✅ 다른 UNIQUE 중복이면 일반 실패 처리(원하면 메시지 다르게)
            log.warn("DuplicateKeyException 발생 (constraint={}): {}", constraint, e.getMessage(), e);
            result.setSuccess(false);
            result.setSmsSent(false);
            result.setAcctStatus(req.getAcctStatus());
            result.setErrorCode("DUPLICATE_KEY");
            result.setErrorMessage("저장에 실패했습니다. (중복 데이터)");
            return result;
        }
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
    
    @Override
    public List<EmpAcctRoleDto> getEmpAcctRoles(String empAcctId) {
        return empAcctMapper.selectEmpAcctRoles(empAcctId);
    }
    
    private String extractOracleConstraintName(Exception e) {
        Throwable t = e;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null) {
                // 예: ORA-00001: unique constraint (DEV.UQ_EMP_ACCT_VEND_LOGIN) violated
                int l = msg.indexOf('(');
                int r = msg.indexOf(')', l + 1);
                if (l >= 0 && r > l) {
                    String inside = msg.substring(l + 1, r); // DEV.UQ_EMP_ACCT_VEND_LOGIN
                    if (inside.contains(".")) {
                        return inside.substring(inside.lastIndexOf('.') + 1).trim();
                    }
                    return inside.trim();
                }
            }
            t = t.getCause();
        }
        return null;
    }
    
}
