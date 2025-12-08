package store.yd2team.insa.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.AllowDucService;
import store.yd2team.insa.service.AllowDucVO;

@Controller
@RequiredArgsConstructor
public class AllowDucController {

    private final AllowDucService allowDucService;

    // ================== 화면 열기 ==================
    @GetMapping("/allowDuc")
    public String allowDucPage() {
        // => /templates/insa/allowDuc.html
        return "insa/allowDuc";
    }

    // ================== 수당 목록 ==================
    @GetMapping("/api/allow/list")
    @ResponseBody
    public List<AllowDucVO> getAllowList() {
        String vendId = LoginSession.getVendId();   // 회사코드
        return allowDucService.selectAllowList(vendId);
    }

    // ================== 공제 목록 ==================
    @GetMapping("/api/duc/list")
    @ResponseBody
    public List<AllowDucVO> getDucList() {
        String vendId = LoginSession.getVendId();
        return allowDucService.selectDucList(vendId);
    }

    // ================== 수당 저장 ==================
    @PostMapping("/api/allow/save")
    @ResponseBody
    public Map<String, Object> saveAllow(@RequestBody Map<String, List<AllowDucVO>> body) {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();    // 사용자 ID

        List<AllowDucVO> createdRows = body.get("createdRows");
        List<AllowDucVO> updatedRows = body.get("updatedRows");
        List<AllowDucVO> deletedRows = body.get("deletedRows");

        allowDucService.saveAllowItems(createdRows, updatedRows, deletedRows, vendId, empId);

        Map<String, Object> result = new HashMap<>();
        result.put("result", "SUCCESS");
        return result;
    }

    // ================== 공제 저장 ==================
    @PostMapping("/api/duc/save")
    @ResponseBody
    public Map<String, Object> saveDuc(@RequestBody Map<String, List<AllowDucVO>> body) {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        List<AllowDucVO> createdRows = body.get("createdRows");
        List<AllowDucVO> updatedRows = body.get("updatedRows");
        List<AllowDucVO> deletedRows = body.get("deletedRows");

        allowDucService.saveDucItems(createdRows, updatedRows, deletedRows, vendId, empId);

        Map<String, Object> result = new HashMap<>();
        result.put("result", "SUCCESS");
        return result;
    }
}
