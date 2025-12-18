package store.yd2team.common.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import store.yd2team.common.dto.SignUpRequestDTO;
import store.yd2team.common.mapper.SignUpMapper;
import store.yd2team.common.service.SignUpService;
import store.yd2team.common.service.VendVO;
import store.yd2team.common.service.EmpAcctVO;
import store.yd2team.insa.service.EmpVO;

@Service
public class SignUpServiceImpl implements SignUpService {
		
	@Autowired
	SignUpMapper signUpMapper;
	
	// 비밀번호 암호화를 위한 PasswordEncoder 주입
	@Autowired
	PasswordEncoder passwordEncoder;
	
	// 아이디 중복체크
	@Override
	public boolean isLoginIdDuplicated(String loginId) {
		if (loginId == null || loginId.isBlank()) {
			return false;
		}
		int count = signUpMapper.countLoginId(loginId);
		return count > 0;
	}
	
	// 사업자등록번호 중복체크
	@Override
	public boolean isBizNoDuplicated(String bizNo) {
		if (bizNo == null || bizNo.isBlank()) {
			return false;
		}
		int count = signUpMapper.countBizNo(bizNo);
		return count > 0;
	}
	
	// 회원가입 처리: tb_vend, tb_emp_acct에 데이터 저장 후 생성된 vendId 반환
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String registerVendor(SignUpRequestDTO dto) throws Exception {
		if (dto == null) {
			throw new IllegalArgumentException("SignUpRequestDTO must not be null");
		}
		
		// 1. vend_id, emp_acct_id PK 생성 (yyyyMM 기준 3자리 시퀀스)
		LocalDate today = LocalDate.now();
		String yyyymm = today.format(DateTimeFormatter.ofPattern("yyyyMM"));
		
		String vendPrefix = "vend_" + yyyymm;
		String empAcctPrefix = "mas_acct_" + yyyymm;
		
		String vendSeq = signUpMapper.getMaxVendSeqOfMonth(vendPrefix);
		int vendNext = 1;
		if (vendSeq != null && !vendSeq.isEmpty()) {
			try {
				vendNext = Integer.parseInt(vendSeq) + 1;
			} catch (NumberFormatException e) {
				vendNext = 1;
			}
		}
		String vendId = vendPrefix + String.format("%03d", vendNext);
		
		String empAcctSeq = signUpMapper.getMaxVendAcctSeqOfMonth(empAcctPrefix);
		int empAcctNext = 1;
		if (empAcctSeq != null && !empAcctSeq.isEmpty()) {
			try {
				empAcctNext = Integer.parseInt(empAcctSeq) + 1;
			} catch (NumberFormatException e) {
				empAcctNext = 1;
			}
		}
		String empAcctId = empAcctPrefix + String.format("%03d", empAcctNext);
		
		// tb_emp용 emp_id는 EmpMapper.setDbAddId와 동일한 규칙(getNextEmpId)으로 생성 (예: emp2512001)
		String empId = signUpMapper.getNextEmpId();
		
		// 2. tb_vend 데이터 매핑 및 저장
		VendVO vend = new VendVO();
		vend.setVendId(vendId);
		vend.setVendNm(dto.getBizName());
		vend.setRpstrNm(dto.getOwnerName());
		vend.setBizno(parseLongSafe(dto.getBizRegNo()));
		vend.setHp(parseLongSafe(dto.getMobileNo()));
		vend.setTel(parseLongSafe(dto.getTelNo()));
		vend.setEmail(dto.getEmail());
		vend.setAddr(dto.getAddr());
		vend.setBizcnd(dto.getBizType());
		
		int vendInsertCount = signUpMapper.insertVend(vend);
		if (vendInsertCount != 1) {
			throw new IllegalStateException("Failed to insert tb_vend record");
		}
		
		// 3. tb_emp(마스터 사원) 먼저 저장하여 FK(parent) 만족
		EmpVO emp = new EmpVO();
		emp.setEmpId(empId);
		emp.setVendId(vendId);
		emp.setNm(dto.getOwnerName());
		emp.setCttpc(formatMobile(dto.getMobileNo()));
		emp.setEmail(dto.getEmail());
		emp.setDty("master");
		emp.setClsf("k6");
		emp.setRspofc("l5");
		emp.setEmplymTy("m4");
		emp.setHffcSt("n1");
		emp.setCreaBy(empId);
		int empInsertCount = signUpMapper.insertMasterEmp(emp);
		if (empInsertCount != 1) {
			throw new IllegalStateException("Failed to insert tb_emp master record");
		}
		
		// 4. tb_emp_acct insert - FK(tb_emp.emp_id) 존재 보장
		EmpAcctVO empAcct = new EmpAcctVO();
		empAcct.setEmpAcctId(empAcctId);
		empAcct.setVendId(vendId);
		empAcct.setEmpId(empId);
		empAcct.setLoginId(dto.getUserId());
		String rawPassword = dto.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);
		empAcct.setLoginPwd(encodedPassword);
		empAcct.setMasYn("e1");
		int empAcctInsertCount = signUpMapper.insertVendAcct(empAcct);
		if (empAcctInsertCount != 1) {
			throw new IllegalStateException("Failed to insert tb_emp_acct record");
		}
		
		return vendId;
	}
	
	private Long parseLongSafe(String value) {
		if (value == null) return null;
		String trimmed = value.trim();
		if (trimmed.isEmpty()) return null;
		try {
			return Long.parseLong(trimmed);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	// 휴대폰 번호를 010-1234-1234 형식으로 변환
	private String formatMobile(String mobileNo) {
		if (mobileNo == null) return null;
		String digits = mobileNo.replaceAll("[^0-9]", "");
		if (digits.length() == 11) {
			return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
		} else if (digits.length() == 10) {
			return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
		}
		// 예상치 못한 길이면 원본 반환
		return mobileNo;
	}

}// end class
