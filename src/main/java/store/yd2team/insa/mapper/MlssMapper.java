package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.MlssVO;

@Mapper
public interface MlssMapper {

	List<MlssVO> empIdList(MlssVO val);
	
	String mlssCreateId();
	
	int insertMlssList(List<MlssVO> val);
	
	List<MlssVO> mlssSearchList(MlssVO val);
	
	int mlssDtChk(@Param("empId") String val);
	
	List<MlssVO> mlssIemList();
}
