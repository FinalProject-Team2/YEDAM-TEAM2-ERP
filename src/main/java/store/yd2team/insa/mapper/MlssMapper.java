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
	
	//페이지 로드시 평가 완료 카운트 로드
	List<MlssVO> mlssStLoadBefore(@Param("vendId") String vendId, @Param("deptId") String deptId, @Param("mlssId") String mlssId, @Param("empId") String empId);
	
	//결과지 확인 눌럿을 때 불러오는 본인 값
	List<MlssVO> FinalResultMlssList(@Param("vendId") String vendId, @Param("deptId") String deptId, @Param("mlssId") String mlssId, @Param("empId") String empId);
	
	//결과 확인 눌럿을 때 불러오는 동료와 상사값
	List<MlssVO> FinalResultMlssEtcList(@Param("vendId") String vendId, @Param("deptId") String deptId, @Param("mlssId") String mlssId, @Param("empId") String empId);
}
