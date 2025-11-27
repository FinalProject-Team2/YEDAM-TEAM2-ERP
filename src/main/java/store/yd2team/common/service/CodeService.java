package store.yd2team.common.service;

import java.util.List;

public interface CodeService {
	
	List<CodeVO> findGrp(CodeVO grpNm);
	
	List<CodeVO> findCode(CodeVO grpId);
}
