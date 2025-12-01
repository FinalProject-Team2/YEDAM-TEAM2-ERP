package store.yd2team.business.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/so")
@RequiredArgsConstructor
public class SoController {

	/* private final PriceService priceService; */

	@GetMapping("/soMain")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/soManage";

	}
/*
	// 조회
	@PostMapping("/list")
	@ResponseBody
	public List<PriceVO> getPriceList(@RequestBody PriceVO vo) {
		
		System.out.println("검색조건 >>> " + vo.toString());
		
		return priceService.getPricePolicyList(vo);
	}
	
	// 공통코드로 정책유형
	@GetMapping("/type-codes")
	@ResponseBody
	public List<CommonCodeVO> getPriceType() {
	    return priceService.getPriceType();
	}
	
	// 등록 및 수정
	@PostMapping("/save")
	@ResponseBody
	public Map<String, Object> savePricePolicy(@RequestBody PriceVO vo) {
	    Map<String, Object> result = new HashMap<>();

	    try {
	        System.out.println("### Controller Request VO : " + vo);

	        int saveResult = priceService.savePricePolicy(vo);

	        result.put("result", saveResult > 0 ? "success" : "success"); // 무조건 success 처리
	        result.put("message", "단가정책 저장 완료");

	    } catch (Exception e) {
	        System.out.println("### Exception : " + e.getMessage());
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    System.out.println("### Final Response : " + result);
	    return result;
	}
	
	// 삭제
	@PostMapping("/delete")
	@ResponseBody
	public Map<String, Object> deletePricePolicy(@RequestBody Map<String, Object> param) {
	    Map<String, Object> result = new HashMap<>();
	    
	    try {
	        List<String> priceIdList = (List<String>) param.get("priceIdList");
	        System.out.println("삭제 요청 ID 리스트 >>> " + priceIdList);

	        priceService.deletePricePolicy(priceIdList);

	        result.put("result", "success");
	    } catch (Exception e) {
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    return result;
	}
*/	
	
}
