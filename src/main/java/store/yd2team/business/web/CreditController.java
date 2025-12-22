package store.yd2team.business.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
   
   // 여신한도관리 메인화면
   @GetMapping("/creditMain")
   public String selectall(Model model) {
       model.addAttribute("test", "testone");
       return "business/credit";
   }
   
   // 조회 고객사 auto complete (ID)
   @GetMapping("/custcomIdSearch")
   @ResponseBody
   public List<CreditVO> searchCustcomId(@RequestParam("keyword") String keyword) {
       return creditService.searchCustcomId(keyword);
   }
   
   // 조회 고객사 auto complete (Name)
   @GetMapping("/custcomNameSearch")
   @ResponseBody
   public List<CreditVO> searchCustcomName(@RequestParam("keyword") String keyword) {
       return creditService.searchCustcomName(keyword);
   }
   
   // 여신현황 / 여신한도 조회
   @PostMapping("/list")
   @ResponseBody
   public List<CreditVO> searchCredit(@RequestBody CreditVO vo) {
       return creditService.searchCredit(vo);
   }
   
   // 업체 상세 조회
   @GetMapping("/custcomDetail")
   @ResponseBody
   public CustcomVO getCustcomDetail(@RequestParam("custcomId") String custcomId) {
       return creditService.getCustcomDetail(custcomId);
   }
   
    // 저장
   @PostMapping("/save")
   @ResponseBody
   public Map<String, Object> saveCreditLimit(@RequestBody List<CreditVO> list) {

       Map<String, Object> result = new HashMap<>();

       try {
           creditService.saveCreditLimit(list);
           result.put("result", "success");
       } catch (Exception e) {
           e.printStackTrace();
           result.put("result", "fail");
           result.put("message", "저장 중 오류가 발생했습니다.");
       }

       return result;
   }
   
   // 출하정지
    @PutMapping("/shipmntStop")
	@ResponseBody
	public Map<String, Object> shipmntStop(@RequestBody CreditVO vo) {
	    Map<String, Object> result = new HashMap<>();

	    try {
	        System.out.println("### Controller Request VO : " + vo);

	        int saveResult = creditService.updateShipmnt(vo);

	        result.put("result", saveResult > 0 ? "success" : "success"); // 무조건 success 처리
	        result.put("message", "출하정지");

	    } catch (Exception e) {
	        System.out.println("### Exception : " + e.getMessage());
	        result.put("result", "fail");
	        result.put("message", e.getMessage());
	    }

	    System.out.println("### Final Response : " + result);
	    return result;
	}


}



