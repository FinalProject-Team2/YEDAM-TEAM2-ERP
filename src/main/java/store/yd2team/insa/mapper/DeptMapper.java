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
	
	//부서 관리 머지문(매치시 업뎃 미스매치시 인서트)
	int mergeDept(DeptVO val);
	
	//머지할때 사원테이블에 부서장과 매치시키는 업데이트문
	int empForMerge(DeptVO val);
	
	//부서 삭제 기능
	int deleteDept(DeptVO val);
}
