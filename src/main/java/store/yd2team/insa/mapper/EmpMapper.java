package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.insa.service.EmpVO;

@Mapper
public interface EmpMapper {

	//다중조건 조회
	List<EmpVO> getListEmpJohoe(EmpVO emp);
	
	//저장(업데이트)
	int setDbEdit(EmpVO emp);
	
	//저장 인서트전 empId생성
	EmpVO setDbAddId();
	
	//저장(인서트)
	int setDbAdd(EmpVO emp);
}
