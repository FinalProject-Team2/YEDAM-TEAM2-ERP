package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.common.service.CodeVO;

@Mapper
public interface CodeMapper {
	List<CodeVO> findGrp(CodeVO grpNm); // 코드 그룹 조회 grp_nm
	List<CodeVO> findCode(CodeVO grpId); // 코드 조회 grp_id
	
}
