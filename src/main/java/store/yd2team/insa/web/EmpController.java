package store.yd2team.insa.web;



import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.EdcService;
import store.yd2team.insa.service.EmpService;
import store.yd2team.insa.service.EmpVO;

@Controller
public class EmpController {
	
	@Autowired EmpService empService;
	@Autowired EdcService edcService;
	
	// ✅ 공통 파일 업로드 처리 메소드
    private void handleFileUpload(EmpVO empVO, MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/upload/images/profil/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // ✅ 사번 기반 파일명 생성
            String fileName = empVO.getEmpId().toLowerCase() + ".jpg";
            File saveFile = new File(uploadDir, fileName);
            photo.transferTo(saveFile);

            // ✅ DB에 저장할 경로 세팅
            empVO.setProofPhoto("/images/profil/" + fileName);
        }
    }

	
	@GetMapping("/emp-register")
	public String empRender(Model model) {
		Map<String, Object> optionMap = edcService.getInputOption();
		SessionDto session = LoginSession.getLoginSession();
	    model.addAttribute("dept", optionMap.get("dept"));   // List<DeptVO>
	    model.addAttribute("clsfLi", optionMap.get("grade"));   // 직급 grade clsf
	    model.addAttribute("rspofcLi", optionMap.get("title"));   // 직책 title rspofc
	    model.addAttribute("emplymTyLi", optionMap.get("emplymTyLi"));   // 고용형태 emplymTyLi emplymTy
	    model.addAttribute("hffcStLi", optionMap.get("hffcStLi"));   // 재직상태 hffcStLi hffcSt
	    model.addAttribute("basiLi", optionMap.get("basiLi"));   // 재직상태 hffcStLi hffcSt
	    model.addAttribute("vendId", session.getVendId());   // 재직상태 hffcStLi hffcSt
		
		
		
		//model.addAttribute("test", "testone");
		return "insa/employee-register";

	}
	
	@GetMapping("/empJohoe")
	@ResponseBody
	public List<EmpVO> empJohoe(@RequestParam("nm") String name, 
			               @RequestParam("empId") String empId, 
			               @RequestParam("deptId") String deptId, 
			               @RequestParam("clsf") String clsf) {
		EmpVO johoeKeyword = new EmpVO();
		String vendId = LoginSession.getVendId();
		johoeKeyword.setVendId(vendId);
		johoeKeyword.setNm(name);
		johoeKeyword.setEmpId(empId);
		johoeKeyword.setDeptId(deptId);
		johoeKeyword.setClsf(clsf);		
		System.out.println("모나오니"+johoeKeyword);
		
		return empService.getListEmpJohoe(johoeKeyword);
	}
	
	@Value("${uploadDir}")
	private String uploadDir;
	
	@PostMapping("/empEdit")
	@ResponseBody
	public List<EmpVO> empRegist(@RequestPart("empVO") EmpVO empVO,
	        @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
		System.out.println("환경설정불러온값"+uploadDir);
		
		SessionDto session = LoginSession.getLoginSession();
		empVO.setVendId(session.getVendId());
		empVO.setUpdtBy(session.getEmpId());
		
		// ✅ 파일 업로드 처리
		handleFileUpload(empVO, photo);

	    // ✅ 사원 정보 DB 저장	      
	    empService.setDbEdit(empVO);
	    EmpVO johoeKeyword = new EmpVO();		
	    johoeKeyword.setVendId(session.getVendId());
		
		return empService.getListEmpJohoe(johoeKeyword);

	}
	
	@PostMapping("/empRegist")
	@ResponseBody
	public List<EmpVO> empRegistAdd(@RequestPart("empVO") EmpVO empVO,
			@RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
		empVO.setEmpId(empService.setDbAddId().getEmpId());
		// ✅ 파일 업로드 처리
		handleFileUpload(empVO, photo);

		empService.setDbAdd(empVO);
		
		EmpVO johoeKeyword = new EmpVO();
		return empService.getListEmpJohoe(johoeKeyword);
	}
}
