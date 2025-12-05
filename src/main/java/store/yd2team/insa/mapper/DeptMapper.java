package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.insa.service.DeptVO;
@Mapper
public interface DeptMapper {

	//거래처별 부서목록조회
	List<DeptVO> getListDept(String val);
}
