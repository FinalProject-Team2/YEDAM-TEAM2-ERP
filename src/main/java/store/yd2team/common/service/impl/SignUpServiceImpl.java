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
		
		// 2. tb_vend 데이터 매핑 (상호명, 대표자, 사업자등록번호, 휴대폰, 대표전화, 이메일, 주소, 업태/업종)
		VendVO vend = new VendVO();
		vend.setVendId(vendId);
		vend.setVendNm(dto.getBizName());
		vend.setRpstrNm(dto.getOwnerName());
		
		// 숫자형 컬럼(Long)으로 매핑 - 숫자만 들어온다는 가정하에 파싱, 실패 시 null
		vend.setBizno(parseLongSafe(dto.getBizRegNo()));
		vend.setHp(parseLongSafe(dto.getMobileNo()));
		vend.setTel(parseLongSafe(dto.getTelNo()));
		
		vend.setEmail(dto.getEmail());
		vend.setAddr(dto.getAddr()); // 화면에서 도로명+상세로 조합된 값
		vend.setBizcnd(dto.getBizType());
		
		int vendInsertCount = signUpMapper.insertVend(vend);
		if (vendInsertCount != 1) {
			throw new IllegalStateException("Failed to insert tb_vend record");
		}
		
		// 3. tb_emp_acct 데이터 매핑 (아이디, 비밀번호, vend_id 등)
		EmpAcctVO empAcct = new EmpAcctVO();
		empAcct.setEmpAcctId(empAcctId);
		empAcct.setVendId(vendId);
		// 최초 가입 계정이므로 empId는 별도 발번 전까지 null 허용 (DB 기본값 사용 또는 추후 업데이트)
		empAcct.setEmpId(null);
		empAcct.setLoginId(dto.getUserId());
		// 평문 비밀번호를 BCrypt로 암호화하여 저장
		String rawPassword = dto.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);
		empAcct.setLoginPwd(encodedPassword);
		// 관리자 여부 mas_yn = 'e1' 고정
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

}// end class