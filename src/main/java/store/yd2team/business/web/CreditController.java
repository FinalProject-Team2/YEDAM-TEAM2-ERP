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
import store.yd2team.business.service.CreditService;
import store.yd2team.business.service.CreditVO;
import store.yd2team.business.service.CustcomVO;

@Controller
@RequestMapping("/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;
	
	@GetMapping("/creditMain")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/credit";
	}
	
	// 조회 고객사 auto complete
	@GetMapping("/custcomIdSearch")
	@ResponseBody
	public List<CreditVO> searchCustcomId(@RequestParam("keyword") String keyword) {
		return creditService.searchCustcomId(keyword);
	}
    
    @GetMapping("/custcomNameSearch")
    @ResponseBody
    public List<CreditVO> searchCustcomName(@RequestParam("keyword") String keyword) {
        return creditService.searchCustcomName(keyword);
    }
    
	// 조회
    @PostMapping("/list")
    @ResponseBody
    public List<CreditVO> searchCustcom(@RequestBody CreditVO vo) {
        return creditService.searchCustcom(vo);
    }
    
    
    // 저장
	/*
	 * @PostMapping("/save")
	 * 
	 * @ResponseBody public Map<String, Object> saveNewCust(@RequestBody CreditVO
	 * vo) { Map<String, Object> result = new HashMap<>();
	 * 
	 * try { System.out.println("### Controller Request VO : " + vo);
	 * 
	 * int saveResult = custcomService.saveNewCust(vo);
	 * 
	 * result.put("result", saveResult > 0 ? "success" : "success"); // 무조건 success
	 * 처리 result.put("message", "신규 고객사 저장 완료");
	 * 
	 * } catch (Exception e) { System.out.println("### Exception : " +
	 * e.getMessage()); result.put("result", "fail"); result.put("message",
	 * e.getMessage()); }
	 * 
	 * System.out.println("### Final Response : " + result); return result; }
	 */
	
}
