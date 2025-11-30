package store.yd2team.insa.mapper;

import java.util.List;

import store.yd2team.insa.service.DeptVO;

public interface DeptMapper {

	//거래처별 부서목록조회
	List<DeptVO> getListDept(String val);
}
