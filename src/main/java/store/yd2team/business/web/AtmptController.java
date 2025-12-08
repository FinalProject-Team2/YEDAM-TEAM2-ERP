package store.yd2team.business.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.AtmptService;
import store.yd2team.business.service.AtmptVO;

@Controller
@RequestMapping("/atmpt")
@RequiredArgsConstructor
public class AtmptController {

    private final AtmptService atmptService;

    // 상세미수관리 메인 화면
    @GetMapping("/atmptMain")
    public String atmptMain(Model model) {
        // 필요 시 공통코드/콤보 데이터 모델에 담아서 내려주기
        return "business/atmpt";   // templates/business/atmpt.html
    }
    
    // 조회 고객사 auto complete (ID)
    @GetMapping("/custcomIdSearch")
    @ResponseBody
    public List<AtmptVO> searchCustcomId(@RequestParam("keyword") String keyword) {
        return atmptService.searchCustcomId(keyword);
    }
    // 조회 고객사 auto complete (Name)
    @GetMapping("/custcomNameSearch")
    @ResponseBody
    public List<AtmptVO> searchCustcomName(@RequestParam("keyword") String keyword) {
        return atmptService.searchCustcomName(keyword);
    }

    // 미수상세조회
    @PostMapping("/list")
    @ResponseBody
    public List<AtmptVO> getAtmptList(@RequestBody AtmptVO searchVO) {
        return atmptService.searchAtmpt(searchVO);
    }
}    