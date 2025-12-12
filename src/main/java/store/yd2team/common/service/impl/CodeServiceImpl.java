package store.yd2team.common.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import store.yd2team.common.aop.SysLog;
import store.yd2team.common.aop.SysLogConfig;
import store.yd2team.common.mapper.CodeMapper;
import store.yd2team.common.service.CodeService;
import store.yd2team.common.service.CodeVO;


@Service
@RequiredArgsConstructor
@SysLogConfig(module = "cm", table = "TB_CODE", pkParam = "codeId")
public class CodeServiceImpl implements CodeService{
	
	private final CodeMapper codeMapper;
	
	// 공통 코드 그룹 조회
	@Override
	public List<CodeVO> findGrp(CodeVO vo) {
		return codeMapper.findGrp(vo);
	}

	// 공통 코드 조회
	@Override
	public List<CodeVO> findCode(CodeVO grpId) {
		return codeMapper.findCode(grpId);
	}
	
	// 등록 가능 체크
	@Override
	public int regYn(String grpId) {
		if(codeMapper.regYn(grpId) > 0) {
			return 1;
		} else {
			return 0;
		}
	}
	
	// 공통 코드 중복 체크
	@Override
	@SysLog(action = "co1", msg = "공통코드 등록", pkFromSession = false, pkField = "grpId")
	public int existsCode(CodeVO vo) {
		return codeMapper.existsCode(vo);
	}
	
	// 공통 코드 등록
	@Override
	@Transactional
	public int regCode(CodeVO vo) {
		// 공통 코드 중복 체크 있으면 스킵
	    if (codeMapper.existsCode(vo) > 0) {
	        return -1;
	    }
	    // 공통 코드 등록
	    try {
	        return codeMapper.regCode(vo);   
	    } catch (DataIntegrityViolationException e) {
	        // DB에서 “데이터 무결성(Data Integrity)”을 깨는 짓을 하면 나는 예외
	        return 0;
	    }
	}
	
	// 공통 코드 수정
	@Override
	@Transactional
	@SysLog(action = "co2", msg = "공통코드 수정", pkFromSession = false, pkField = "grpId")
	public int modifyCode(CodeVO vo) {
		
		if (codeMapper.existsCode(vo) > 0) {
	        return -1; 
	    }
		
		try {
	        return codeMapper.modifyCode(vo);   
	    } catch (DataIntegrityViolationException e) {
	        return 0;
	    }
	}

}
