package store.yd2team.common.service;

public interface EmpAcctService {

	boolean checkPassword(String vendId, String loginId, String rawPassword);

	void changePassword(String vendId, String loginId, String rawNewPassword);

	void clearTempPasswordFlag(String vendId, String loginId);
	
}
