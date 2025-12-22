// src/main/java/store/yd2team/insa/web/SalyLedgController.java
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
import org.springframework.web.bind.annotation.RequestParam;
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
        return "insa/salyLedg";
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
     * 급여대장 목록 조회
     *  - URL : GET /insa/saly/list
     *  - Query:
     *      salyLedgNm, payDtStart, payDtEnd
     * =========================== */
    @GetMapping("/insa/saly/list")
    @ResponseBody
    public List<SalyLedgVO> list(
            @RequestParam(value = "salyLedgNm", required = false) String salyLedgNm,
            @RequestParam(value = "payDtStart", required = false) String payDtStart,
            @RequestParam(value = "payDtEnd", required = false) String payDtEnd,
            HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) {
            log.warn("[salyList] 세션 없음");
            return List.of();
        }

        String vendId = login.getVendId();
        return salyLedgService.getSalyLedgList(vendId, salyLedgNm, payDtStart, payDtEnd);
    }

    /* ===========================
     * 급여대장 상세 조회 (수정용)
     *  - URL : GET /insa/saly/detail?salyLedgId=...
     *  - 응답: SalyLedgVO + empIdList
     * =========================== */
    @GetMapping("/insa/saly/detail")
    @ResponseBody
    public SalyLedgVO detail(@RequestParam("salyLedgId") String salyLedgId,
                             HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) {
            log.warn("[salyDetail] 세션 없음");
            return null;
        }

        String vendId = login.getVendId();
        return salyLedgService.getSalyLedgDetail(salyLedgId, vendId);
    }

    /* ===========================
     * 모달: 사원 목록 조회
     *  - URL : /insa/saly/empList
     *  - Body: { deptId, empNm }
     * =========================== */
    @PostMapping("/insa/saly/empList")
    @ResponseBody
    public List<EmpVO> empList(@RequestBody Map<String, String> body,
                               HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) {
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
     *    (salyLedgId, salyLedgNm, revsYm, payDt, empIdList ...)
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
        String loginEmp = login.getEmpId();

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

    /* ===========================
     * 급여대장 삭제 (명세서 포함)
     *  - URL : /insa/saly/delete
     *  - Body: { salyLedgId }
     * =========================== */
    @PostMapping("/insa/saly/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestBody Map<String, String> body,
                                      HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        String salyLedgId = body.get("salyLedgId");
        if (salyLedgId == null || salyLedgId.isBlank()) {
            res.put("result", "FAIL");
            res.put("message", "삭제할 급여대장 ID가 없습니다.");
            return res;
        }

        try {
            salyLedgService.deleteSalyLedg(salyLedgId, login.getVendId());
            res.put("result", "SUCCESS");
        } catch (Exception e) {
            log.error("급여대장 삭제 중 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }
    @PostMapping("/insa/saly/confirm")
    @ResponseBody
    public Map<String, Object> confirm(@RequestBody Map<String, String> body,
                                       HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다.");
            return res;
        }

        try {
            salyLedgService.confirmSalyLedg(
                body.get("salyLedgId"),
                login.getVendId(),
                login.getEmpId()
            );
            res.put("result", "SUCCESS");
        } catch (Exception e) {
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }
        return res;
    }

    @PostMapping("/insa/saly/cancelConfirm")
    @ResponseBody
    public Map<String, Object> cancelConfirm(@RequestBody Map<String, String> body,
                                             HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다.");
            return res;
        }

        salyLedgService.cancelConfirmSalyLedg(
            body.get("salyLedgId"),
            login.getVendId(),
            login.getEmpId()
        );

        res.put("result", "SUCCESS");
        return res;
    }

}
