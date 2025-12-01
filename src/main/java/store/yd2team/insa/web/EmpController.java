package store.yd2team.insa.web;



import java.io.IOException;
import java.util.List;

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

import store.yd2team.insa.service.EmpService;
import store.yd2team.insa.service.EmpVO;

@Controller
public class EmpController {
	
	@Autowired EmpService empService;
	
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
		
		
		
		
		//model.addAttribute("test", "testone");
		return "insa/employee-register";

	}
	
	@GetMapping("/empJohoe")
	@ResponseBody
	public List<EmpVO> empJohoe(@RequestParam("nm") String name, 
			               @RequestParam("empId") String empId, 
			               @RequestParam("deptNm") String deptNm, 
			               @RequestParam("clsf") String clsf) {
		EmpVO johoeKeyword = new EmpVO();
		johoeKeyword.setNm(deptNm);
		johoeKeyword.setEmpId(empId);
		johoeKeyword.setDeptId(deptNm);
		johoeKeyword.setClsf(clsf);		
		
		return empService.getListEmpJohoe(johoeKeyword);
	}
	
	@Value("${uploadDir}")
	private String uploadDir;
	
	@PostMapping("/empEdit")
	@ResponseBody
	public List<EmpVO> empRegist(@RequestPart("empVO") EmpVO empVO,
	        @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
		System.out.println("환경설정불러온값"+uploadDir);
		
		// ✅ 파일 업로드 처리
		handleFileUpload(empVO, photo);


	    // ✅ 사원 정보 DB 저장	      
	    empService.setDbEdit(empVO);
	    EmpVO johoeKeyword = new EmpVO();
		
		
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
