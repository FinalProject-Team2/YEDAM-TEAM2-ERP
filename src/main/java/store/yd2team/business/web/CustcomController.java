package store.yd2team.business.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.CustcomService;
import store.yd2team.business.service.CustcomVO;

@Controller
@RequestMapping("/custcom")
@RequiredArgsConstructor
public class CustcomController {

    private final CustcomService custcomService;
    
    // 메인화면
	@GetMapping("/manage")
	public String selectall() {
		
		return "business/custManage";

	}

	// 조회
    @PostMapping("/list")
    @ResponseBody
    public List<CustcomVO> searchCustcom(@RequestBody CustcomVO vo) {
        return custcomService.searchCustcom(vo);
    }
    
	// 공통코드로 정책유형
	@GetMapping("/bs-codes")
	@ResponseBody
	public List<CustcomVO> getBSType() {
	    return custcomService.getBSType();
	}
    
    // 저장
    @PostMapping("/save")
	@ResponseBody
	public Map<String, Object> saveNewCust(@RequestBody CustcomVO vo) {
	    Map<String, Object> result = new HashMap<>();

	    try {
	        System.out.println("### Controller Request VO : " + vo);

	        int saveResult = custcomService.saveNewCust(vo);

	        result.put("result", saveResult > 0 ? "success" : "success"); // 무조건 success 처리
	        result.put("message", "신규 고객사 저장 완료");

	    } catch (Exception e) {
	        System.out.println("### Exception : " + e.getMessage());
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    System.out.println("### Final Response : " + result);
	    return result;
	}
}
