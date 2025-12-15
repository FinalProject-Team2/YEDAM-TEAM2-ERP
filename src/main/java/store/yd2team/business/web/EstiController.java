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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.EstiSoDetailVO;
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
		return "business/estiManage";

	}
	
	// 견적서 조회(그리드)
    @PostMapping("/list")
    @ResponseBody
    public List<EstiSoVO> estiList(@RequestBody EstiSoVO cond) {
        return estiSoService.selectEstiList(cond);
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
	
    // 견적서 모달 고객사 auto complete
    @GetMapping("/custcomIdSearch")
    @ResponseBody
    public List<EstiSoVO> searchCustcomId(@RequestParam("keyword") String keyword) {
        return estiSoService.searchCustcomId(keyword);
    }
    
    @GetMapping("/custcomSearch")
    @ResponseBody
    public List<EstiSoVO> searchCustcomName(@RequestParam("keyword") String keyword) {
        return estiSoService.searchCustcomName(keyword);
    }

    
    
    
    // 저장 (신규/수정 공통, 이력 INSERT 방식)
    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveEsti(@RequestBody EstiSoVO vo) {

        String estiId = estiSoService.saveEsti(vo);

        return Map.of(
            "success", true,
            "estiId", estiId
        );
    }
    
    // 이력보기 모달
    @GetMapping("/history/{estiId}")
    @ResponseBody
    public List<EstiSoVO> getEstiHistory(
            @PathVariable("estiId") String estiId
    ) {
        return estiSoService.getEstiHistory(estiId);
    }
    
    // 이력보기의 보기 버튼
    @GetMapping("/detail/{estiId}/{version}")
    @ResponseBody
    public EstiSoVO getEstiByVersion(
            @PathVariable("estiId") String estiId,
            @PathVariable("version") String version
    ) {
        return estiSoService.getEstiByVersion(estiId, version);
    }
	/*
	 * @PostMapping("/save") public ResponseEntity<Map<String, Object>>
	 * saveEsti(@RequestBody EstiSoVO vo) {
	 * 
	 * // 로그인 사용자ID는 프로젝트 방식에 맞춰 교체 String loginId = "testUser";
	 * vo.setCreaBy(loginId); vo.setUpdtBy(loginId);
	 * 
	 * String estiId = estiSoService.saveEsti(vo);
	 * 
	 * Map<String, Object> result = new HashMap<>(); result.put("success", true);
	 * result.put("estiId", estiId);
	 * 
	 * return ResponseEntity.ok(result); }
	 */

    /** estiId 기준 최신버전 조회 */
    @GetMapping("/{estiId}")
    @ResponseBody
    public EstiSoVO getEsti(@PathVariable("estiId") String estiId) {
        return estiSoService.getEsti(estiId);
    }
	/*
	 * @GetMapping("/{estiId}") public ResponseEntity<EstiSoVO>
	 * getEsti(@PathVariable String estiId) { return
	 * ResponseEntity.ok(estiSoService.getEsti(estiId)); }
	 */
    
    /** 견적서 수정 모달 - 헤더 + 상세 목록 조회 */
	
    @GetMapping("/detail/{estiId}")
    @ResponseBody
    public Map<String, Object> getEstiDetail(
            @PathVariable("estiId") String estiId
    ) {

        // 1) 헤더 조회 (최신 버전)
        EstiSoVO header = estiSoService.getEsti(estiId);

        // 2) 상세 조회
        List<EstiSoDetailVO> detailList = header != null
                ? header.getDetailList()
                : List.of();

        // 3) 응답 구조 통일
        Map<String, Object> result = new HashMap<>();
        result.put("header", header);
        result.put("detailList", detailList);

        return result;
    }
	 
    
}
