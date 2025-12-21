package store.yd2team.insa.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.MlssRequestVO;
import store.yd2team.insa.service.MlssService;
import store.yd2team.insa.service.MlssVO;

@Controller
public class MlssController {
	
	@Autowired MlssService mlssService;

	private int rankValue(String clsf) {
	    switch (clsf) {
	        case "k1": return 1; // 사원
	        case "k2": return 2; // 대리
	        case "k3": return 3; // 과장
	        case "k4": return 4; // 부장
	        case "k5": return 5; // 이사
	        default: return 0;
	    }
	}
	
	@GetMapping("/mlss")
	public String mlssRender(Model model, RedirectAttributes redirectAttributes) {
		SessionDto session = LoginSession.getLoginSession();
		if(session == null) {
			return "common/logIn";
		}
		
		String empId = session.getEmpId();
		List<MlssVO> empList = mlssService.mlssEmpList( empId, session.getDeptId() );
		String targetClsf = "";
		Iterator<MlssVO> it = empList.iterator();
		while (it.hasNext()) {
			MlssVO vo = it.next();
		    if (vo.getEmpId().equals(empId)) {
		        // 삭제 전에 값 저장
		        targetClsf = vo.getClsf();
		        // 안전하게 삭제
		        it.remove();
		        break; // 한 건만 삭제한다면 break
		    }
		}
		List<MlssVO> uprList = new ArrayList<>();
		List<MlssVO> lwrList = new ArrayList<>();

		int targetRank = rankValue(targetClsf);

		for (MlssVO vo : empList) {
		    int voRank = rankValue(vo.getClsf());
		    if (voRank > targetRank) {
		        uprList.add(vo); // 상급
		    } else {
		        lwrList.add(vo); // 같거나 하급
		    }
		}
		MlssVO v = mlssService.mlssVisitChk( empId );				
		if( v != null && v.getMlssId() != null) {	
			
		} else {
			redirectAttributes.addFlashAttribute("msg", "평가 기간이 아닙니다");
			return "redirect:/";
		}
		Map<String, List<MlssVO>> list = mlssService.mlssLoadBefore();
		List<MlssVO> userWrterList = mlssService.mlssWrterLoadBefore(v.getMlssEmpId(), empId);
		List<MlssVO> stList = mlssService.mlssStLoadBefore(session.getVendId(), session.getDeptId(), v.getMlssId(), empId);
		model.addAttribute("userWrterList", userWrterList);
		model.addAttribute("stList", stList);
		model.addAttribute("evaleRelateUpr", uprList);
		model.addAttribute("evaleRelateLwr", lwrList);
		model.addAttribute("empId", empId);
		model.addAttribute("mlssId", v.getMlssId());
		model.addAttribute("mlssEmpId", v.getMlssEmpId());
		model.addAttribute("evlBeginDt", v.getEvlBeginDt());
		model.addAttribute("evlEndDt", v.getEvlEndDt());
		model.addAttribute("iemList", list);
		model.addAttribute("Session", "testone");
		return "insa/mlss";

	}
	
	@GetMapping("/mlss/master")
	public String mlssMasterRender(Model model) {
		
		String empId = LoginSession.getEmpId();
		String vendId = LoginSession.getVendId();
		String deptId = LoginSession.getDeptId();
		List<String> userInfo = new ArrayList<>();
		userInfo.add(empId);
		userInfo.add(vendId);
		userInfo.add(deptId);		
		model.addAttribute("userInfo", userInfo);
		return "insa/mlss-regist-list";

	}
	
	//다면평가 등록
	@PostMapping("/mlssRegist")
	@ResponseBody
	public int mlssRegist(@RequestBody MlssVO keyword) {		
		return mlssService.mlssRegist(keyword);	
	}
	
	//다중입력조회
		@GetMapping("/mlssListJohoe")
		@ResponseBody
		public List<MlssVO> mlssJohoe(MlssVO keyword) {			
			System.out.println("모나오니 다중입력조회"+keyword);				
			return mlssService.mlssListJohoe(keyword);			
		}
		
	//다면평가 평가하는 페이지 관련
	//다면평가 평가한후 등록하는 기능		
		@PostMapping("/mlssUserRegist")
		@ResponseBody
		public int mlssUserRegist(@RequestBody MlssRequestVO keyword) {	
			
			return mlssService.mlssWrterRegist(keyword);	
		}
		
	//평가 탭 누를때 불러오는 조회기능(결과지 확인 제외)
		@GetMapping("/selectMlssChkJohoe")
		@ResponseBody
		public List<MlssVO> selectMlssChkJohoe(MlssVO keyword) {			
			System.out.println("모나오니 다중입력조회"+keyword);			
			return mlssService.mlssWrterLoadBefore(keyword.getMlssEmpId(), keyword.getEmpId());		
		}
		
	//결과지 확인 눌럿을 때 불러오는 Data
			@GetMapping("/mlssFinalResult")
			@ResponseBody
			public List<MlssVO> mlssFinalData(Model model) {	
				SessionDto session = LoginSession.getLoginSession();
				MlssVO v = mlssService.mlssVisitChk( session.getEmpId() );							
				return mlssService.FinalResultMlssList(session.getVendId(), session.getDeptId(), v.getMlssId(), session.getEmpId());
			}
	

}
