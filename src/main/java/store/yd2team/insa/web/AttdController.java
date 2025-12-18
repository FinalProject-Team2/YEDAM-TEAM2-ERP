package store.yd2team.insa.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.service.AttdStatusService;
import store.yd2team.insa.service.AttdStatusVO;

@Controller
@RequiredArgsConstructor
public class AttdController {

    private final AttdStatusService attdStatusService;

    @GetMapping("/attd")
    public String selectall(Model model) {

        // HR 관리자 여부를 화면에 내려줘서 검색조건 노출 제어
        boolean isHrAdmin = attdStatusService.isHrAdminCurrentUser();
        model.addAttribute("isHrAdmin", isHrAdmin);

        return "insa/attd";
    }

    // ✅ 근태 조회 Ajax
    @PostMapping("/attd/list")
    @ResponseBody
    public List<AttdStatusVO> selectAttdList(@RequestBody AttdStatusVO searchVO) {
        return attdStatusService.selectAttdStatusList(searchVO);
    }
}
