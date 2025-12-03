package store.yd2team.common.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.common.dto.CodeRegResponseDto;
import store.yd2team.common.service.CodeService;
import store.yd2team.common.service.CodeVO;

@RequiredArgsConstructor
@RestController
public class CodeController {

	final CodeService codeService;

	// 공통 코드 그룹 조회
	@PostMapping("/code/grpNm")
	public List<CodeVO> commonCodeGrp(@RequestBody CodeVO vo) {
		return codeService.findGrp(vo);
	}

	// 공통 코드 목록 조회
	@PostMapping("/code/codeId")
	public List<CodeVO> commonCode(@RequestBody CodeVO grpId) {
		return codeService.findCode(grpId);
	}

	// 자동 완성
	@GetMapping("/code/auto")
	@ResponseBody
	public List<CodeVO> auto(@RequestParam("keyword") String keyword) {
		CodeVO cond = new CodeVO();
		cond.setGrpNm(keyword);
		return codeService.findGrp(cond);
	}

	// 공통 코드 등록
	@PostMapping("/code/regCode")
	public CodeRegResponseDto regCode(@RequestBody CodeVO vo) {
		
		String vendId = LoginSession.getVendId();
	    String empId  = LoginSession.getEmpId();

	    vo.setVendId(vendId);  
	    vo.setCreaBy(empId);    
		
		String chkId = vo.getGrpId();

		int chk = codeService.regYn(chkId);

		if (chk == 0) {
			return CodeRegResponseDto.fail("등록이 불가능한 코드 그룹입니다.");
		}

		int result = codeService.regCode(vo);
		
		if (result == -1) {
	        // 코드명 중복
	        return CodeRegResponseDto.fail("이미 존재하는 코드 명입니다.");
	    }
		
		// 중복 or 실패(데이터 무결성 catch)
		if (result == 0) {
			return CodeRegResponseDto.fail("코드 등록 중 오류가 발생했습니다.");
		}

		// 성공 (vo.getCodeId() 에 값 세팅해두었다고 가정)
		return CodeRegResponseDto.ok(vo.getCodeId());
	}
	
	// 공통 코드 수정
	@PostMapping("/code/modCode")
	public CodeRegResponseDto modifyCode(@RequestBody CodeVO vo) {

	    // grpId / codeId 둘 다 들어왔는지 최소 체크
	    if (vo.getGrpId() == null || vo.getCodeId() == null) {
	        return CodeRegResponseDto.fail("그룹 ID 또는 코드 ID가 없습니다.");
	    }
	    
	    String vendId = LoginSession.getVendId();
	    String empId  = LoginSession.getEmpId();

	    vo.setVendId(vendId);
	    vo.setUpdtBy(empId);

	    int result = codeService.modifyCode(vo);
	    
	    if (result == -1) {
	        // 코드명 중복
	        return CodeRegResponseDto.fail("이미 존재하는 코드 명입니다.");
	    }
	    
	    if (result == 0) {
	        // WHERE 조건에 해당하는 행이 없을 때 (이미 삭제됐거나 잘못된 ID)
	        return CodeRegResponseDto.fail("수정할 코드가 없습니다.");
	    }

	    // 성공
	    return CodeRegResponseDto.ok(vo.getCodeId());
	}

}
