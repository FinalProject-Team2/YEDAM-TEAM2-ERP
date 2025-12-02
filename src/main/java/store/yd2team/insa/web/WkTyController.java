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
import store.yd2team.insa.service.HldyVO;
import store.yd2team.insa.service.WkTyService;

@Controller
@RequiredArgsConstructor
public class WkTyController {

    private final WkTyService wkTyService;

    // 화면 열기
    @GetMapping("/wkTy")
    public String wkTyPage() {
        return "/insa/wkTy";
    }

    // 휴일 기준 목록 조회
    @GetMapping("/api/hldy/list")
    @ResponseBody
    public List<HldyVO> getHldyList() {
        return wkTyService.getHlDyList();
    }

    // 휴일 기준 저장 (추가/수정/삭제)
    @PostMapping("/api/hldy/save")
    @ResponseBody
    public Map<String, Object> saveHldy(
            @RequestBody Map<String, List<HldyVO>> body) {

        // ★ 세션에서 공통값 꺼냄
        String empId  = LoginSession.getEmpId();   // 생성자/수정자
        String vendId = LoginSession.getVendId();  // 회사 코드

        List<HldyVO> createdRows = body.get("createdRows");
        List<HldyVO> updatedRows = body.get("updatedRows");
        List<HldyVO> deletedRows = body.get("deletedRows");

        // 신규
        if (createdRows != null) {
            for (HldyVO vo : createdRows) {
                if (vo.getHldyNm() != null) {
                    vo.setHldyNm(vo.getHldyNm().trim());
                }
                vo.setCreaBy(empId);
                vo.setVendId(vendId);
                wkTyService.insertHlDy(vo);
            }
        }

        // 수정
        if (updatedRows != null) {
            for (HldyVO vo : updatedRows) {
                if (vo.getHldyNm() != null) {
                    vo.setHldyNm(vo.getHldyNm().trim());
                }
                vo.setUpdtBy(empId);
                wkTyService.updateHlDy(vo);
            }
        }

        // 삭제
        if (deletedRows != null) {
            for (HldyVO vo : deletedRows) {
                if (vo.getHldyNo() != null) {
                    wkTyService.deleteHlDy(vo.getHldyNo());
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("result", "OK");
        return result;
    }
}
