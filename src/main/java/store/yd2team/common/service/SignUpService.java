package store.yd2team.common.service;

import store.yd2team.common.dto.SignUpRequestDTO;

public interface SignUpService {
	
	// 아이디 중복 여부 체크 (중복이면 true, 아니면 false)
	boolean isLoginIdDuplicated(String loginId);
	
	// 사업자등록번호 중복 여부 체크 (중복이면 true, 아니면 false)
	boolean isBizNoDuplicated(String bizNo);
	
	// 회원가입 처리: tb_vend, tb_vend_acct에 데이터 저장 후 생성된 vendId 반환
	String registerVendor(SignUpRequestDTO dto) throws Exception;

}// end interface