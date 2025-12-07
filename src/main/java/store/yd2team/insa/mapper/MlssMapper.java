package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.EmpVO;
import store.yd2team.insa.service.MlssVO;

@Mapper
public interface MlssMapper {

	List<MlssVO> empIdList(MlssVO val);
	
	String mlssCreateId();
	
	int insertMlssList(List<MlssVO> val);
	
	List<MlssVO> mlssSearchList(MlssVO val);
	
	String mlssDtChk(@Param("empId") String val);
	
	List<MlssVO> mlssIemList();
	
	int mlssWrterRegist(List<MlssVO> val);
	
	List<EmpVO> mlssEmpList(@Param("deptId") String val);
	
	int mlssMasterUpdate(MlssVO val);
	
	List<MlssVO> mlssWrterLoadBefore(@Param("mlssId")String mlssId, @Param("empId")String empId);
}
