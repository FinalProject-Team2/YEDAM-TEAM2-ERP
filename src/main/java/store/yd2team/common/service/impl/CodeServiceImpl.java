package store.yd2team.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.mapper.CodeMapper;
import store.yd2team.common.service.CodeService;
import store.yd2team.common.service.CodeVO;


@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService{
	
	private final CodeMapper codeMapper;
	
	@Override
	public List<CodeVO> findGrp(CodeVO grpNm) {
		return codeMapper.findGrp(grpNm);
	}

	@Override
	public List<CodeVO> findCode(CodeVO grpId) {
		return codeMapper.findCode(grpId);
	}
	
	
}
