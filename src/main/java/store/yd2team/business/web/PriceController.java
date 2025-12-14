package store.yd2team.business.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceService;
import store.yd2team.business.service.PriceVO;
import store.yd2team.common.util.LoginSession;


@Controller
@RequestMapping("/price")
@RequiredArgsConstructor
public class PriceController {

	private final PriceService priceService;

	@GetMapping("/priceMain")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/priceManage";

	}

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
	
	
	// ============================== 고객사모달
	// 1) 고객사 detail 저장
	@PostMapping("/policy/save")
	@ResponseBody
	public Map<String, Object> savePricePolicyDetail(@RequestBody PriceVO vo) {

	    Map<String, Object> result = new HashMap<>();

	    try {
	        // detailList 유효성 검사
	        if (vo.getDetailList() == null || vo.getDetailList().isEmpty()) {
	            result.put("result", "fail");
	            result.put("message", "고객사를 1개 이상 선택해야 합니다.");
	            return result;
	        }

	        priceService.savePricePolicyDetail(vo);

	        result.put("result", "success");
	        result.put("message", "고객사 단가정책 저장 완료");

	    } catch (Exception e) {
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    return result;
	}


	// 2) 고객사 detail 조회 (모달 오픈 시 체크박스 자동 체크용)
	@GetMapping("/policy/detail")
	@ResponseBody
	public Map<String, Object> selectPricePolicyDetail(@RequestParam("priceId") String priceId) {
	    Map<String, Object> result = new HashMap<>();

	    try {
	        List<Map<String, Object>> list = priceService.selectPricePolicyDetail(priceId);
	        result.put("result", "success");
	        result.put("detailList", list);
	    } catch (Exception e) {
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    return result;
	}
	
	
	// ====================================== 상품 모달
	// 상품모달 기본 조회
	@PostMapping("/product/list")
	@ResponseBody
	public List<Map<String, Object>> selectProductList(@RequestBody Map<String,Object> param) {

	    String productName = param.get("productName") != null ?
	            param.get("productName").toString() : "";

	    return priceService.selectProductList(productName);
	}
	
	// 상품 저장
	@PostMapping("/policy/saveProduct")
	@ResponseBody
	public Map<String, Object> savePricePolicyProduct(@RequestBody PriceVO vo) {

	    Map<String, Object> result = new HashMap<>();

	    try {
	        priceService.savePricePolicyProduct(vo);

	        result.put("result", "success");
	        result.put("message", "상품 단가정책 저장 완료");

	    } catch (Exception e) {
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    return result;
	}
	
	// 2) 상품 detail 조회 (모달 오픈 시 체크박스 자동 체크)
	@GetMapping("/policy/product")
	@ResponseBody
	public Map<String, Object> selectPricePolicyProduct(@RequestParam("priceId") String priceId) {

	    Map<String, Object> result = new HashMap<>();

	    try {
	        List<Map<String, Object>> list = priceService.selectPricePolicyProduct(priceId);

	        result.put("result", "success");
	        result.put("detailList", list);   // [{productId: "..."}]
	    } catch (Exception e) {
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    return result;
	}
	
	
	// 할인 조회
	@PostMapping("/discount")
    @ResponseBody
    public Map<String, Object> getDiscount(@RequestBody PriceVO vo) {

        // 세션 정보
        String vendId = LoginSession.getVendId();
        vo.setVendId(vendId);

        PriceVO result = priceService.getMaxDiscount(vo);

        if (result == null || result.getDcRate() == null) {
            return Map.of("dcRate", 0);
        }

        return Map.of(
            "dcRate", result.getDcRate(),
            "pricePolicyId", result.getPricePolicyId(),
            "policyType", result.getPolicyType()
        );
    }
}
