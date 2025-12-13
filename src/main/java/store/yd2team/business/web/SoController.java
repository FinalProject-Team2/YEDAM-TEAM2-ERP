package store.yd2team.business.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.EstiSoService;
import store.yd2team.business.service.EstiSoVO;
import store.yd2team.business.service.OustVO;


@Controller
@RequestMapping("/so")
@RequiredArgsConstructor
public class SoController {

	 private final EstiSoService estiSoService; 
	 
	@GetMapping("/soMain")
	public String soMain() {
	    return "business/soManage";
	}
	
	// 주문 모달 초기 데이터 (견적 → 주문)
	@GetMapping("/fromEsti/{estiId}")
    @ResponseBody
    public Map<String, Object> getOrderFromEsti(@PathVariable("estiId") String estiId) {

        EstiSoVO header = estiSoService.getOrderInitFromEsti(estiId);

        return Map.of(
            "header", header,
            "detailList", header.getDetailList()
        );
    }

    // 주문 저장
	@PostMapping("/fromEsti/save")
    @ResponseBody
    public Map<String, Object> saveOrderFromEsti(@RequestBody EstiSoVO vo) {
		// 1) 필수값 검증
	    if (vo.getEstiId() == null || vo.getEstiId().isBlank()) {
	        throw new IllegalArgumentException("견적서 ID는 필수입니다.");
	    }

	    if (vo.getRciptAppoDt() == null) {
	        throw new IllegalArgumentException("입금약속일은 필수입니다.");
	    }

	    if (vo.getRciptAppoAmt() == null || vo.getRciptAppoAmt() <= 0) {
	        throw new IllegalArgumentException("입금약속금액은 0보다 커야 합니다.");
	    }

	    if (vo.getAppoMtd() == null || vo.getAppoMtd().isBlank()) {
	        throw new IllegalArgumentException("결제방법은 필수입니다.");
	    }


        String soId = estiSoService.saveOrderFromEsti(vo);

        return Map.of(
            "success", true,
            "soId", soId
        );
    }
	/*
	 * @PostMapping("/fromEsti/save")
	 * 
	 * @ResponseBody public Map<String, Object> saveOrderFromEsti(@RequestBody
	 * EstiSoVO vo) {
	 * 
	 * String soId = estiSoService.saveOrderFromEsti(vo);
	 * 
	 * Map<String, Object> result = new HashMap<>(); result.put("success", true);
	 * result.put("soId", soId);
	 * 
	 * return result; }
	 */
    
	
	// 주문서 조회 (AJAX)
    @PostMapping("/list")
    @ResponseBody   // JSON으로 리턴하기 위해 필요
    public List<EstiSoVO> selectSoList(@RequestBody EstiSoVO vo) {
        return estiSoService.selectSoList(vo);
    }
    
    // 주문서관리 승인버튼
    @PostMapping("/approve")
    @ResponseBody
    public Map<String, Object> approveSo(@RequestBody List<EstiSoVO> list) {

        try {
            estiSoService.approveSo(list);
            return Map.of("success", true);

        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", e.getMessage()
            );
        }
    }
    
    // 보류버튼 이벤트
    @PostMapping("/reject")
    @ResponseBody
    public Map<String, Object> rejectOrder(@RequestBody Map<String, Object> param) {

        String soId = (String) param.get("soId");
        String reason = (String) param.get("reason");

        return estiSoService.rejectOrder(soId, reason);
    }
    
    // 주문취소버튼 이벤트
    @PostMapping("/cancel")
    @ResponseBody
    public Map<String, Object> cancelOrder(@RequestBody Map<String, Object> param) {

        String soId = (String) param.get("soId");
        String reason = (String) param.get("reason");

        return estiSoService.cancelOrder(soId, reason);
    }
    
 // 출하지시서 작성 버튼 저장 이벤트
 	@PostMapping("/oust/save")
 	@ResponseBody
 	public Map<String, Object> saveOust(@RequestBody OustVO vo) {
 	    try {
 	        estiSoService.saveOust(vo);
 	        return Map.of("success", true);
 	    } catch (Exception e) {
 	        return Map.of("success", false, "message", e.getMessage());
 	    }
 	}
    
    
}
