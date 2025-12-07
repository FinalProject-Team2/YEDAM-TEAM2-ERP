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


@Controller
@RequestMapping("/so")
@RequiredArgsConstructor
public class SoController {

	 private final EstiSoService estiSoService; 

	@GetMapping("/soMain")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/soManage";

	}
	
	 // 주문 모달 초기 데이터 (견적 → 주문)
    @GetMapping("/fromEsti/{estiId}")
    @ResponseBody
    public Map<String, Object> getOrderFromEsti(@PathVariable("estiId") String estiId) {

        EstiSoVO header = estiSoService.getOrderInitFromEsti(estiId);

        Map<String, Object> result = new HashMap<>();
        result.put("header", header);
        result.put("detailList", header.getDetailList());

        return result;
    }

    // 주문 저장
    @PostMapping("/fromEsti/save")
    @ResponseBody
    public Map<String, Object> saveOrderFromEsti(@RequestBody EstiSoVO vo) {

        String soId = estiSoService.saveOrderFromEsti(vo);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("soId", soId);

        return result;
    }
    
    
	// 주문서 조회(그리드)
	/*
	 * @PostMapping("/list")
	 * 
	 * @ResponseBody public List<EstiSoVO> soList(@RequestBody EstiSoVO so) {
	 * 
	 * System.out.println("검색조건 >>> " + so);
	 * 
	 * // 서비스 호출 List<EstiSoVO> soList = estiSoService.selectSoList(so);
	 * 
	 * // JSON 으로 반환 (뷰 이름 X) return soList; }
	 */
	
	// 주문서 조회 (AJAX)
    @PostMapping("/list")
    @ResponseBody   // JSON으로 리턴하기 위해 필요
    public List<EstiSoVO> selectSoList(@RequestBody EstiSoVO vo) {
        return estiSoService.selectSoList(vo);
    }
    
    // 주문서관리 승인버튼
    /** 주문 승인 */
    @PostMapping("/approve")
    @ResponseBody
    public Map<String, Object> approveOrders(@RequestBody Map<String, Object> param) {

        List<String> soIds = (List<String>) param.get("soIds");

        return estiSoService.approveOrders(soIds);
    }
}
