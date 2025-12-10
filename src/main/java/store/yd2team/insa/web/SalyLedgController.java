package store.yd2team.insa.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.insa.service.EmpVO;
import store.yd2team.insa.service.SalyLedgService;
import store.yd2team.insa.service.SalyLedgVO;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SalyLedgController {

    private final SalyLedgService salyLedgService;

    /** 급여대장 화면 진입 */
    @GetMapping("/salyLedg")
    public String selectall(Model model) {
        model.addAttribute("test", "testone");
        return "/insa/salyLedg";
    }

    /* ===========================
     * 공통: 세션 정보 조회
     * =========================== */
    private SessionDto getLogin(HttpSession session) {
        Object obj = session.getAttribute(SessionConst.LOGIN_EMP);
        if (obj instanceof SessionDto) {
            return (SessionDto) obj;
        }
        return null;
    }

    /* ===========================
     * 모달: 사원 목록 조회
     *  - URL : /insa/saly/empList
     *  - Body: { deptId, empNm }
     *  - 세션의 vend_id + 재직(hffc_st='n1') 필터
     * =========================== */
    @PostMapping("/insa/saly/empList")
    @ResponseBody
    public List<EmpVO> empList(@RequestBody Map<String, String> body,
                               HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) {
            // 세션 없으면 빈 리스트 리턴
            log.warn("[salyEmpList] 세션 없음");
            return List.of();
        }

        String vendId = login.getVendId();
        String deptId = body.getOrDefault("deptId", "");
        String empNm  = body.getOrDefault("empNm", "");

        return salyLedgService.getEmpListForSaly(vendId, deptId, empNm);
    }

    /* ===========================
     * 급여대장 + 급여명세서 저장
     *  - URL : /insa/saly/save
     *  - Body: SalyLedgVO
     *    (salyLedgId, salyLedgNm, revsYm, payDt, deptId, empIdList ...)
     * =========================== */
    @PostMapping("/insa/saly/save")
    @ResponseBody
    public Map<String, Object> save(@RequestBody SalyLedgVO vo,
                                    HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        String vendId   = login.getVendId();
        String loginEmp = login.getEmpId();   // 필요 시 필드명 맞춰서 수정

        try {
            String salyLedgId = salyLedgService.saveSalyLedg(vo, vendId, loginEmp);

            res.put("result", "SUCCESS");
            res.put("salyLedgId", salyLedgId);
            res.put("rcnt", vo.getEmpIdList() != null ? vo.getEmpIdList().size() : 0);
        } catch (Exception e) {
            log.error("급여대장 저장 중 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }
}
