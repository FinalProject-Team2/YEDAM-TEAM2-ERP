package store.yd2team.business.web;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.ChurnStdrVO;
import store.yd2team.business.service.MonthlySalesDTO;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.common.util.LoginSession;

@Controller
public class BusinessController {
	@Autowired
	BusinessService businessService;

	@GetMapping("/samplepage")
	public String sample(Model model, ChurnStdrVO churn) {
		System.out.println("=== BusinessController.insert() 호출됨 ===");
		model.addAttribute("test", "testone");
		return "business/samplepage"; // /는 빼도 됨
	}
	@GetMapping("/debug/session")
	@ResponseBody
	public String debugSession() {
	    return "vendId=" + LoginSession.getVendId() + ", empId=" + LoginSession.getEmpId();
	}
	//휴면,이탈객 기준등록 페이지열람
	@GetMapping("/churnRiskStdrRegister")
	public String insert(Model model, ChurnStdrVO churn) {
		System.out.println("=== BusinessController.getChurnStdrList() 호출됨 ===");
		List<ChurnStdrVO> getChurnStdrList = businessService.getChurnStdrList(churn);
		model.addAttribute("getChurnStdrList", getChurnStdrList);
		return "business/churnRiskStdrRegister"; // /는 빼도 됨
	}
	//휴면,이탈 기준 수정
	@PostMapping("/churnRiskStdrRegister/update")
	@ResponseBody
	public int updateChurnStdrList(@RequestBody ChurnStdrVO req) {
	    return businessService.updateChurnStdrList(
	        req.getDormancyList(),
	        req.getChurnList(),
	        LoginSession.getVendId(),
	        LoginSession.getEmpId()
	    );
	}


	//
	@GetMapping("/churnRiskList")
	public String churnRiskListForm(Model model) {
	    model.addAttribute("getMonthlySalesList", Collections.emptyList());
	    model.addAttribute("cond", new MonthlySalesDTO());
	    return "business/churnRiskList";
	}

	// 휴면,이탈고객 검색조회
	@PostMapping("/churnRiskList")
	public String selectall(@ModelAttribute("cond") MonthlySalesDTO cond, Model model) {
	    System.out.println("=== churnRiskList.selectall() 호출됨 ===");
	    // 휴면/이탈 조건별 점수화 + 검색조건 적용
	    List<MonthlySalesDTO> getMonthlySalesList = businessService.getMonthlySalesChange(cond);
	    model.addAttribute("getMonthlySalesList", getMonthlySalesList);
	    // cond는 @ModelAttribute("cond")라서 이미 모델에 같이 들어감(그래도 명시해도 됨)
	    // model.addAttribute("cond", cond);

	    return "business/churnRiskList";
	}

	//
	//
	//
	// 잠재고객 기준상세목록 가져오기 및 페이지열람
	@GetMapping("/potentialCustRegister")
	public String churnRiskStdrRegister(Model model) {
		model.addAttribute("test", "testone");
    	return "business/potentialCustRegister";
	}
	//
//	// 상세 전체 조회 (그리드 4개 공통)
    @RequestMapping("/potentialCustRegister/get")
    @ResponseBody
    public List<PotentialStdrVO> getPotentialStdrDetailList(PotentialStdrVO cond) {
        return businessService.getPotentialStdrDetailList(cond);
    }
    // 상세 등록, 수정
    @RequestMapping("/potentialCustRegister/save")
    @ResponseBody
    public String savePotentialStdrDetailList(@RequestBody List<PotentialStdrVO> list) {
    	businessService.savePotentialStdrDetailList(list);
        return "OK";
    }
    // 상세 삭제
    @PostMapping("/business/potentialStdr/delete")
    @ResponseBody
    public int deletePotentialStdr(@RequestBody List<String> idList) {
        return businessService.deletePotentialStdrList(idList);
    }

    
    
	//
	// 잠재고객 검색조회 페이지열람
	@GetMapping("/potentialCustList")
	public String list(Model model) {
		System.out.println("=== BusinessController.list() 호출됨 ===");
	  //model.addAttribute("list", businessService.getList());
		model.addAttribute("test", "testone");
		
		return "business/potentialCustList";
	}
	// 잠재고객 검색조회
	@PostMapping("/potentialCustList")
	public String stdrlist(BusinessVO vo, Model model) {
		
		System.out.println("=== BusinessController.stdrlist() 호출됨 ===");
		List<BusinessVO> potentialstdrList = businessService.getBusinessList(vo);
		// 위에서 span이 쓰는 list도 채워주기
//		model.addAttribute("list", potentialstdrList);
		model.addAttribute("potentialstdrList", potentialstdrList);
		model.addAttribute("stdrvo", vo);
		
		return "business/potentialCustList";
	}
	// 잠재고객데이터매핑
	@PostMapping("/potential/sync")
	public String sync() {
		businessService.fetchAndSaveFromApi();
		return "redirect:/potentialCustList";
	}
	//
	//
	//영업활동관리 페이지 열람
	@GetMapping("/salesActivity")
	public String update1(BusinessVO vo, Model model) {
	    // 페이지 처음 로딩 시 완전히 빈 리스트 제공
	    model.addAttribute("getMonthlySalesList", Collections.emptyList());
	    return "business/salesactivity";
	}
	//영업활동관리.고객조회
	@PostMapping("/salesActivity")
	public String saelesPotential(BusinessVO vo, Model model) {
		System.out.println("=== salesActivity.getBusinessList() 호출됨 ===");
		List<BusinessVO> potentialstdrList = businessService.getBusinessList(vo);
	
		// 위에서 span이 쓰는 list도 채워주기
		model.addAttribute("potentialstdrList", potentialstdrList);
		model.addAttribute("stdrvo", vo);
		
		return "business/salesactivity";
	}
}


