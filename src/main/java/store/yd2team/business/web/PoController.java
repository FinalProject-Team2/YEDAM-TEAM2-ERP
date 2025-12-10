package store.yd2team.business.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.OustService;
import store.yd2team.business.service.OustVO;


@Controller
@RequestMapping("/po")
@RequiredArgsConstructor
public class PoController {

	/* private final OustService oustService; */
	
	// 메인화면로드
	@GetMapping("/poMain")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/poManage";

	}
	
	// 조회
	/*
	 * @PostMapping("/list")
	 * 
	 * @ResponseBody public List<OustVO> selectOust(@RequestBody OustVO vo) {
	 * 
	 * System.out.println("검색조건 >>> " + vo.toString());
	 * 
	 * return oustService.selectOust(vo); }
	 */
	
}
