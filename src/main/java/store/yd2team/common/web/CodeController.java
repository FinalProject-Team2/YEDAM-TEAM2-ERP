package store.yd2team.common.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.service.CodeService;
import store.yd2team.common.service.CodeVO;

@RequiredArgsConstructor
@RestController
public class CodeController {

	final CodeService codeService;

	// 공통 코드 그룹 조회
	@PostMapping("/code/grpNm")
	public List<CodeVO> commonCodeGrp(@RequestBody CodeVO grpNm) {
		return codeService.findGrp(grpNm);
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

}
