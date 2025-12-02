package store.yd2team.business.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.EstiSoService;
import store.yd2team.business.service.EstiSoVO;


@Controller
@RequestMapping("/esti")
@RequiredArgsConstructor
public class EstiController {

	private final EstiSoService estiSoService;

	// 견적서 메인
	@GetMapping("/estiMain")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/estiManage";

	}
	
	// 견적서 조회(그리드)
	@PostMapping("/list")
    @ResponseBody
    public List<EstiSoVO> estiList(@RequestBody EstiSoVO cond) {

        System.out.println("검색조건 >>> " + cond);

        // 서비스 호출
        List<EstiSoVO> estiList = estiSoService.selectEstiList(cond);

        // JSON 으로 반환 (뷰 이름 X)
        return estiList;
    }
	
	// 그리드 견적서 상태 업데이트
	@PostMapping("/updateStatus")
	@ResponseBody
	public int updateStatus(@RequestBody EstiSoVO vo) {
	    return estiSoService.updateStatus(vo);
	}
	
	// 견적서 모달 상품 auto complete
	@GetMapping("/productSearch")
	@ResponseBody
    public List<EstiSoVO> searchProduct(@RequestParam("keyword") String keyword) {
        return estiSoService.searchProduct(keyword);
    }

    @GetMapping("/detail")
    public EstiSoVO getProductDetail(@RequestParam("productId") String productId) {
        return estiSoService.getProductDetail(productId);
    }
	
	
	
/*
	
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
