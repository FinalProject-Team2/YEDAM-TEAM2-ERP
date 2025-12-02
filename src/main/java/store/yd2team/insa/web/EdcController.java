package store.yd2team.insa.web;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.yd2team.common.util.LoginSession;
import store.yd2team.common.web.SysLogController;
import store.yd2team.insa.service.EdcService;
import store.yd2team.insa.service.EdcVO;

@Controller
public class EdcController {

    private final SysLogController sysLogController;
	
	@Autowired EdcService edcService;

    EdcController(SysLogController sysLogController) {
        this.sysLogController = sysLogController;
    }
	
	//법정교육조회 관리자페이지 출력
	@GetMapping("/edc")
	public String edcRender(Model model) {	
		Map<String, Object> optionMap = edcService.getInputOption();
		
	    model.addAttribute("dept", optionMap.get("dept"));   // List<DeptVO>
	    model.addAttribute("grade", optionMap.get("grade")); // List<CodeVO>
	    model.addAttribute("title", optionMap.get("title")); // List<CodeVO>		
				
		return "insa/edc";

	}
	
	
	//법정교육조회 사용자페이지 출력
	@GetMapping("/edcUser")
	public String edcUserRender(Model model) {			
		System.out.println("모나오니"+LoginSession.getEmpId());
		model.addAttribute("empId", LoginSession.getEmpId());
		return "insa/edcUser";

	}
	
	//다중입력조회
	@GetMapping("/edcJohoe")
	@ResponseBody
	public List<EdcVO> edcJohoe(EdcVO johoeKeyword) {
		
		System.out.println("모나오니 다중입력조회"+johoeKeyword);
		/*
		 * EdcVO johoeKeyword = new EdcVO(); 
		 * johoeKeyword.setEdcNm(edcNm);
		 * johoeKeyword.setEdcTy(edcTy); 
		 * johoeKeyword.setEdcBeginDt(edcBeginDt);
		 */
			
		
		return edcService.getListEdcJohoe(johoeKeyword);
	}
	
	//교육 대상자들 조회
		@GetMapping("/edcDetaJohoe")
		@ResponseBody
		public List<EdcVO> edcDetaJohoe(EdcVO keyword) {
			
			System.out.println("모나오니" + keyword);				
			
			return edcService.getListEdcDetaJohoe(keyword);
		}
		
	//교육 대상자 등록
		@PostMapping("/edcIdRegist")
		@ResponseBody
		public List<EdcVO> empRegistAdd(@RequestBody EdcVO keyword) throws ParseException {
			System.out.println("모나오니" + keyword);	
			edcService.setDbEdcAdd(keyword);
			
			EdcVO johoeKeyword = new EdcVO();
			johoeKeyword.setEdcNm("");
			johoeKeyword.setEdcTy("법정의무교육");
			String s = "2025-01-01";
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(s);
			johoeKeyword.setEdcBeginDt(d);   // ✅ Date 타입 그대로 전달   
					return edcService.getListEdcJohoe(johoeKeyword);
		}
		
	//교육 대상자들 조회
			@GetMapping("/edcUserJohoe")
			@ResponseBody
			public List<EdcVO> edcUserJohoe(EdcVO keyword) {							
				
				return edcService.getListEdcUserJohoe(keyword);
			}
			
	//교육 대상자가 보낸 pdf파일을 받은후 처리
			@PostMapping("/edcUser/uploadPdf")
			@ResponseBody
			public boolean edcUploadPdf(@RequestParam("pdfFile") MultipartFile pdfFile,
			                         @RequestParam("edcUserData") String edcUserJson) throws IllegalStateException, IOException {
				
				// JSON 문자열을 VO로 변환
			    ObjectMapper mapper = new ObjectMapper();
			    EdcVO edcUserData = mapper.readValue(edcUserJson, EdcVO.class);
				
				// 파일명 추출
			    String fileName = edcUserData.getEdcId() + edcUserData.getEmpId();
			    System.out.println(fileName);
			    // 저장 경로 지정
			    String uploadDir = System.getProperty("user.dir") + "\\upload\\pdf\\";
			    File dir = new File(uploadDir);
			    if (!dir.exists()) dir.mkdirs();

			    // 실제 저장
			    File dest = new File(uploadDir + fileName+ ".pdf");
			    pdfFile.transferTo(dest);

			    // VO에 상대 경로 저장
			    edcUserData.setComplFile("/pdf/" + fileName + ".pdf");

			    
			   // PDF 내용 읽기
			    try (PDDocument document = PDDocument.load(dest)) {
			        PDFTextStripper stripper = new PDFTextStripper();
			        String pdfText = stripper.getText(document);

			        // edcNm 값이 PDF 텍스트에 포함되어 있는지 확인
			        if (pdfText.contains(edcUserData.getEdcNm())) {
			            System.out.println("일치합니다");
			            // DB 업데이트 로직...
			            edcUserData.setComplSt("수료");
					    edcService.edcUserRegistChk(edcUserData);
			            return true; // PDF 안에 해당 문구가 있으면 true 반환
			        }
			    }
			    
			    
			    edcUserData.setComplSt("반려");
			    // DB 업데이트 로직...
			    edcService.edcUserRegistChk(edcUserData);

			    
			    return false;
			}
}
