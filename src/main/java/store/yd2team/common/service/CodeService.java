package store.yd2team.common.service;

import java.util.List;

public interface CodeService {
	
	List<CodeVO> findGrp(CodeVO vo); // 그룹 코드 조회
	
	List<CodeVO> findCode(CodeVO grpId); // 코드 조회 by grpId
	
	int regYn(String grpId); // 등록 가능 체크
	int existsCode(CodeVO vo); // 코드 중복 체크
	int regCode(CodeVO vo); // 코드 등록
	
	int modifyCode(CodeVO vo); // 코드 수정
}
