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
	
	MlssVO mlssDtChk(@Param("empId") String val);
	
	List<MlssVO> mlssIemList();
	
	int mlssWrterRegist(List<MlssVO> val);
	
	List<MlssVO> mlssEmpList(@Param("empId") String empId, @Param("deptId") String deptId);
	
	int mlssMasterUpdate(MlssVO val);
	
	List<MlssVO> mlssWrterLoadBefore(@Param("mlssEmpId")String mlssEmpId, @Param("empId")String empId);
	
	//mlss 마스터 등록
	int insertMlssHead(MlssVO vo);
}
