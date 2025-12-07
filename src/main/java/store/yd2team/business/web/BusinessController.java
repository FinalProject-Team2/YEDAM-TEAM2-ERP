package store.yd2team.business.web;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.ContactVO;
import store.yd2team.business.service.MonthlySalesDTO;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.business.service.churnRiskVO;

@Controller
public class BusinessController {
	@Autowired
	BusinessService businessService;
	/*
	 * @GetMapping("/churnRiskStdrRegister") public String insert(Model model) {
	 *
	 * model.addAttri bute("test", "testone"); return
	 * "/business/churnRiskStdrRegister"; // return "여기까지는 BusinessController 들어옴";
	 * }
	 */
	@GetMapping("/samplepage")
	public String sample(Model model) {
		System.out.println("=== BusinessController.insert() 호출됨 ===");
		model.addAttribute("test", "testone");
		return "business/samplepage"; // /는 빼도 됨
	}
	@GetMapping("/churnRiskStdrRegister")
	public String insert(Model model) {
		System.out.println("=== BusinessController.insert() 호출됨 ===");
		model.addAttribute("test", "testone");
		return "business/churnRiskStdrRegister"; // /는 빼도 됨
	}
	//
	//
	//휴면,이탈 조회페이지열람
	@GetMapping("/churnRiskList")
	public String churnRiskListForm(Model model) {
	    // 페이지 처음 로딩 시 완전히 빈 리스트 제공
	    model.addAttribute("getMonthlySalesList", Collections.emptyList());
	    return "business/churnRiskList";
	}
	// 휴면,이탈고객검색조회
	@PostMapping("/churnRiskList")
	public String selectall(churnRiskVO vo, MonthlySalesDTO cond, Model model) {
		System.out.println("=== churnRiskList.selectall() 호출됨 ===");
		  // 휴먼/이탈 조건별 점수화 + 검색조건 적용
	    List<MonthlySalesDTO> getMonthlySalesList = businessService.getMonthlySalesChange(cond);
	    model.addAttribute("getMonthlySalesList", getMonthlySalesList);
	    // 검색조건 다시 화면에 뿌려주고 싶으면
	    model.addAttribute("cond", cond);
	    return "business/churnRiskList";
	}
	//
	//
	//
	// 잠재고객기준상세목록조회
	@GetMapping("/potentialCustRegister")
	public String churnRiskStdrRegister(Model model) {
		   List<PotentialStdrVO> allList = businessService.getStdrDetailAll();
		    List<PotentialStdrVO> industryList  = new ArrayList<>();
		    List<PotentialStdrVO> sizeList      = new ArrayList<>();
		    List<PotentialStdrVO> establishList = new ArrayList<>();
		    List<PotentialStdrVO> regionList    = new ArrayList<>();
		    for (PotentialStdrVO vo : allList) {
		        String id = vo.getStdrId();
		        if ("STDR-001".equals(id)) {
		            industryList.add(vo);      // 업종
		        }
		         else if ("STDR-002".equals(id))
		         {
		            sizeList.add(vo);          // 규모
		        }
		         else if ("STDR-003".equals(id))
		         {
		            establishList.add(vo);     // 설립
		        }
		         else if ("STDR-004".equals(id))
		         {
		            regionList.add(vo);        // 지역/상권
		        }
		    }
		    model.addAttribute("industryList",  industryList);
		    model.addAttribute("sizeList",      sizeList);
		    model.addAttribute("establishList", establishList);
		    model.addAttribute("regionList",    regionList);
	    	return "business/potentialCustRegister";
	}
	// 페이지열람시 잠재고객전체조회
	@GetMapping("/potentialCustList")
	public String list(Model model) {
		System.out.println("=== BusinessController.list() 호출됨 ===");
		model.addAttribute("list", businessService.getList());
		return "business/potentialCustList";
	}
	// 잠재고객검색조회
	@PostMapping("/potentialCustList")
	public String stdrlist(BusinessVO vo, Model model) {
		
		System.out.println("=== BusinessController.list() 호출됨 ===");
		List<BusinessVO> potentialstdrList = businessService.getBusinessList(vo);
		// 위에서 span이 쓰는 list도 채워주기
		model.addAttribute("list", potentialstdrList);
		model.addAttribute("potentialstdrList", potentialstdrList);
		model.addAttribute("stdrvo", vo);
		
		return "business/potentialCustList";
	}
	//
	//잠재고객조건상세목록수정
	@PostMapping("/potentialCustRegister")
	@ResponseBody
	public Map<String, List<BusinessVO>> saveAll(
	        @RequestBody List<BusinessVO> list) {
		businessService.saveAll(list);
	    Map<String, List<BusinessVO>> result = new HashMap<>();
	    result.put("industryList",  businessService.getListByCond("IND"));
	    result.put("sizeList",      businessService.getListByCond("SIZE"));
	    result.put("establishList", businessService.getListByCond("EST"));
	    result.put("regionList",    businessService.getListByCond("REG"));
	    return result;
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
		System.out.println("=== salesActivity.saelesAction() 호출됨 ===");
	
		List<BusinessVO> potentialstdrList = businessService.getBusinessList(vo);
	
		// 위에서 span이 쓰는 list도 채워주기
		model.addAttribute("potentialstdrList", potentialstdrList);
		model.addAttribute("stdrvo", vo);
		
		return "business/salesactivity";
	}
	//영업활동관리.접촉사항
	@PostMapping("/salesActivity/contact")
	public String getAction(Model model) {
		System.out.println("=== salesActivity.saelesAction() 호출됨 ===");
		List<ContactVO> contactrList = businessService.getAction();
		// 위에서 span이 쓰는 list도 채워주기
		model.addAttribute("contactrList", contactrList);
//		model.addAttribute("stdrvo", vo);
		
		return "business/salesactivity";
	}
	//영업활동관리.리드분석
	@PostMapping("/salesActivity/LeadGenerar")
	public String getLeadgenerar(Model model) {
		System.out.println("=== salesActivity.saelesAction() 호출됨 ===");
	
		List<ContactVO> contactrList = businessService.getAction();
	
		// 위에서 span이 쓰는 list도 채워주기
		model.addAttribute("contactrList", contactrList);
//			model.addAttribute("stdrvo", vo);
		
		return "business/salesactivity";
	}
}
	


