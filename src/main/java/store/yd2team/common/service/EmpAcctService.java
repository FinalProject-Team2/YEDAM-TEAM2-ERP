package store.yd2team.common.service;

import java.util.List;

import store.yd2team.common.dto.EmpAcctEmployeeDto;
import store.yd2team.common.dto.EmpAcctRoleDto;
import store.yd2team.common.dto.EmpAcctSaveRequestDto;
import store.yd2team.common.dto.EmpAcctSaveResultDto;
import store.yd2team.common.dto.EmpDeptDto;

public interface EmpAcctService {

	boolean checkPassword(String vendId, String loginId, String rawPassword);

	void changePassword(String vendId, String loginId, String rawNewPassword);

	void clearTempPasswordFlag(String vendId, String loginId);
	
	List<EmpAcctEmployeeDto> searchEmployees(String vendId,
								             String deptName,
								             String jobName,
								             String empName,
								             String loginId);
	
	List<EmpDeptDto> findEmpDeptList(String vendId);
	
	List<EmpAcctEmployeeDto> autocompleteEmpName(String vendId, String keyword);

    List<EmpAcctEmployeeDto> autocompleteLoginId(String vendId, String keyword);
    
    EmpAcctSaveResultDto saveEmpAccount(EmpAcctSaveRequestDto req, String loginEmpId);
    
    List<EmpAcctRoleDto> getEmpAcctRoles(String empAcctId);

}
