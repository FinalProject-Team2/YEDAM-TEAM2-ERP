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
	public List<CodeVO> commonCode(@RequestBody CodeVO vo) {
		
		System.out.println(vo);
		String vendId = LoginSession.getVendId();
		vo.setVendId(vendId);
		
		return codeService.findCode(vo);
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
		
	    if (vendId == null || vendId.isBlank()) {
	        return CodeRegResponseDto.fail(
	            "현재 계정에는 업체 정보가 없어 코드 등록이 불가능합니다.\n" +
	            "관리자에게 문의해 주세요."
	        );
	    }
	    
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
			return CodeRegResponseDto.fail(
				"시스템 오류로 코드 등록에 실패했습니다.\n" +
			    "잠시 후 다시 시도하거나 관리자에게 문의해 주세요."
			);
		}

		// 성공 (vo.getCodeId() 에 값 세팅해두었다고 가정)
		return CodeRegResponseDto.ok(vo.getCodeId());
	}
	
	@PostMapping("/code/modCode")
	public CodeRegResponseDto modifyCode(@RequestBody CodeVO vo) {

	    // 0. 필수 값 체크 (grpId / codeId)
	    if (vo.getGrpId() == null || vo.getCodeId() == null) {
	        return CodeRegResponseDto.fail("그룹 ID 또는 코드 ID가 없습니다.");
	    }

	    // 1. 세션의 vendId / empId 조회
	    String vendId = LoginSession.getVendId();
	    String empId  = LoginSession.getEmpId();

	    // 1-1. 업체 정보가 없는 계정은 수정 자체를 막기
	    if (vendId == null || vendId.isBlank()) {
	        return CodeRegResponseDto.fail(
	            "현재 계정에는 업체 정보가 없어 코드 수정이 불가능합니다.\n" +
	            "관리자에게 문의해 주세요."
	        );
	    }

	    vo.setVendId(vendId);
	    vo.setUpdtBy(empId);

	    // 2. 실제 수정 처리
	    int result = codeService.modifyCode(vo);

	    if (result == -1) {
	        // 코드명 중복
	        return CodeRegResponseDto.fail("이미 존재하는 코드 명입니다.");
	    }

	    if (result == 0) {
	        // 0: WHERE 조건에 맞는 행이 없거나(삭제/다른 업체 코드 등)
	        //    또는 DB 제약 오류 등으로 실패한 경우
	        return CodeRegResponseDto.fail(
	            "수정할 코드가 없거나 수정 권한이 없습니다.\n" +
	            "문제가 계속되면 관리자에게 문의해 주세요."
	        );
	    }

	    // 3. 성공
	    return CodeRegResponseDto.ok(vo.getCodeId());
	}

}
