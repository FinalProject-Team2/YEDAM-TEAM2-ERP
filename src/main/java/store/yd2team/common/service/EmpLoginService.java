package store.yd2team.common.service;

import store.yd2team.common.dto.EmpLoginResultDto;

public interface EmpLoginService {

	EmpLoginResultDto login(String vendId, String loginId, String password);

	boolean isCaptchaRequired(String vendId, String loginId);
	
	SecPolicyVO getSecPolicy(String vendId);
	
	void increaseLoginFailByOtp(EmpAcctVO empAcct);
}
