package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.insa.service.DeptVO;
import store.yd2team.insa.service.EmpVO;
@Mapper
public interface DeptMapper {

	//거래처별 부서목록조회
	List<DeptVO> getListDept(String val);
	
	//조직도에 쓸 데이터 조회
	List<EmpVO> getOrgRenderList(String val);
	
	//부서 관리에 부서장 제외한 사원목록조회
	List<DeptVO> getNonManagerEmployeeIds(String val);
}
