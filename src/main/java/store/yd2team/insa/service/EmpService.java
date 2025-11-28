package store.yd2team.insa.service;

import java.util.List;

public interface EmpService {

	//다중조건조회
	List<EmpVO> getListEmpJohoe(EmpVO emp);
	
	//db저장
	int setDbEdit(EmpVO emp);
}
