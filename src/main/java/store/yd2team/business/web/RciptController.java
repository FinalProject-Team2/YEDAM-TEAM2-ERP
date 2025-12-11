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
import store.yd2team.business.service.RciptService;
import store.yd2team.business.service.RciptVO;

@Controller
@RequestMapping("/rcipt")
@RequiredArgsConstructor
public class RciptController {

    private final RciptService rciptService;

    /**
     * 미수채권관리 메인 화면
     */
    @GetMapping("/rciptMain")
    public String rciptMain(Model model) {
        // 필요 시 공통코드/콤보 데이터 모델에 담아서 내려주기
        return "business/rcipt";   // templates/business/atmpt.html
    }
    
    // 조회 고객사 auto complete (ID)
    @GetMapping("/custcomIdSearch")
    @ResponseBody
    public List<RciptVO> searchCustcomId(@RequestParam("keyword") String keyword) {
        return rciptService.searchCustcomId(keyword);
    }
    // 조회 고객사 auto complete (Name)
    @GetMapping("/custcomNameSearch")
    @ResponseBody
    public List<RciptVO> searchCustcomName(@RequestParam("keyword") String keyword) {
        return rciptService.searchCustcomName(keyword);
    }

    /* 미수채권 목록 조회 (/atmpt/list)
     * - 검색조건: 고객코드, 고객사, 담당자, 기간(fromDt, toDt) 등을 AtmptVO에 포함
     * - 결과: 그리드에 뿌릴 미수/입출금 내역 리스트
     */
    @PostMapping("/list")
    @ResponseBody
    public List<RciptVO> getRciptList(@RequestBody RciptVO searchVO) {
        return rciptService.searchRcipt(searchVO);
    }

   
    // 저장
    @PostMapping("/save")
	@ResponseBody
	public Map<String, Object> saveRciptDetail(@RequestBody RciptVO vo) {
    	
    	
	    Map<String, Object> result = new HashMap<>();

	    try {
	        System.out.println("### Controller Request VO : " + vo);

	        int saveResult = rciptService.insertRciptDetail(vo);

	        result.put("result", saveResult > 0 ? "success" : "success"); // 무조건 success 처리
	        result.put("message", "입금상세저장");

	    } catch (Exception e) {
	        System.out.println("### Exception : " + e.getMessage());
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    System.out.println("### Final Response : " + result);
	    return result;
	}
}
