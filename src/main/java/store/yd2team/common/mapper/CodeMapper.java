package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.common.service.CodeVO;

@Mapper
public interface CodeMapper {
	List<CodeVO> findGrp(CodeVO vo); // 코드 그룹 조회 grp_nm
	
	List<CodeVO> findCode(CodeVO grpId); // 코드 조회 grp_id
	
	int regYn(String grpId); // 등록 가능 체크
	int existsCode(CodeVO vo); // 코드 중복 체크
	int regCode(CodeVO vo); // 코드 등록
	
	int modifyCode(CodeVO vo); // 코드 수정
}
