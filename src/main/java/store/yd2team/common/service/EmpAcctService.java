package store.yd2team.common.service;

import store.yd2team.common.dto.LoginResultDto;

public interface EmpAcctService {

	LoginResultDto login(String vendId, String loginId, String password);

	boolean isCaptchaRequired(String vendId, String loginId);
	
	SecPolicyVO getSecPolicy(String vendId);
}
