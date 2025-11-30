package store.yd2team.common.service;

public interface SignUpService {
	
	// 아이디 중복 여부 체크 (중복이면 true, 아니면 false)
	boolean isLoginIdDuplicated(String loginId);
	
	// 사업자등록번호 중복 여부 체크 (중복이면 true, 아니면 false)
	boolean isBizNoDuplicated(String bizNo);

}// end interface