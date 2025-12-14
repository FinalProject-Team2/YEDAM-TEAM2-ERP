package store.yd2team.insa.service;

import java.util.List;

public interface DeptService {

	List<DeptVO> getListDept(String vendId);
	
	List<DeptVO> getNonManagerEmployeeIds(String vendId);
	
	int mergeDept(List<DeptVO> val);
	
	int deleteDept(List<DeptVO> val);
}
