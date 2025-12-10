package store.yd2team.insa.service;

import java.util.List;

public interface EmpService {

	//다중조건조회
	List<EmpVO> getListEmpJohoe(EmpVO emp);
	
	//db저장
	int setDbEdit(EmpVO emp);
	
	//db저장Id생성
	EmpVO setDbAddId();
	
	//db저장추가
	int setDbAdd(EmpVO emp);
	
	//조직도 불러올 데이터 조회
	List<EmpVO>  getOrgRenderList(String val);
}
