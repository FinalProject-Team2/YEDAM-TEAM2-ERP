package store.yd2team.insa.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.insa.service.AllowDucVO;
import store.yd2team.insa.service.CalGrpVO;
import store.yd2team.insa.service.SalyCalcService;
import store.yd2team.insa.service.SalySpecCalcViewVO;
import store.yd2team.insa.service.SalySpecItemVO;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SalyCalcController {

    private final SalyCalcService salyCalcService;

    private SessionDto getLogin(HttpSession session) {
        Object obj = session.getAttribute(SessionConst.LOGIN_EMP);
        if (obj instanceof SessionDto) return (SessionDto) obj;
        return null;
    }

    @GetMapping("/insa/saly/calc/specList")
    public List<SalySpecCalcViewVO> specList(@RequestParam("salyLedgId") String salyLedgId,
                                             HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) return List.of();

        return salyCalcService.getSalySpecCalcList(salyLedgId, login.getVendId());
    }

    @GetMapping("/insa/saly/calc/empRows")
    public List<Map<String, Object>> empRows(@RequestParam("salyLedgId") String salyLedgId,
                                             HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) return List.of();

        List<SalySpecCalcViewVO> list = salyCalcService.getSalySpecCalcList(salyLedgId, login.getVendId());
        if (list == null) return List.of();

        return list.stream().map(vo -> {
            Map<String, Object> row = new HashMap<>();
            row.put("salySpecId", vo.getSalySpecId());   // ✅ 추가
            row.put("empId", vo.getEmpId());
            row.put("empNm", vo.getEmpNm());
            row.put("deptNm", vo.getDeptNm());
            row.put("clsfNm", vo.getClsfNm());           // ✅ 추가
            row.put("rspofcNm", vo.getRspofcNm());       // ✅ 추가
            row.put("calcGrpNm", "");
            return row;
        }).toList();
    }

    // ✅ 라디오 버튼(급여계산그룹) 목록
    @GetMapping("/insa/saly/calc/grp/list")
    public List<CalGrpVO> calcGrpList(HttpSession session) {
        SessionDto login = getLogin(session);
        if (login == null) return List.of();
        return salyCalcService.getCalcGroupList(login.getVendId());
    }

    // ✅ 급여계산그룹 모달(체크박스): 수당/공제 항목 목록
    @GetMapping("/insa/saly/item/list")
    public List<Map<String, Object>> itemList(@RequestParam(value = "grpNo", required = false) Long grpNo,
                                              HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) return List.of();

        List<AllowDucVO> vos = salyCalcService.getAllowDucList(login.getVendId(), grpNo);

        return (vos == null ? List.<AllowDucVO>of() : vos).stream().map(vo -> {
            Map<String, Object> m = new HashMap<>();

            boolean isAllow = (vo.getAllowId() != null && !vo.getAllowId().isBlank());
            m.put("itemTy", isAllow ? "A" : "D");
            m.put("itemId", isAllow ? vo.getAllowId() : vo.getDucId());
            m.put("itemNm", isAllow ? vo.getAllowNm() : vo.getDucNm());
            m.put("dispNo", vo.getDispNo());
            m.put("calFmlt", vo.getCalFmlt());
            m.put("calMthd", vo.getCalMthd());
            m.put("ynCode", vo.getYnCode());
            return m;
        }).toList();
    }

    // ✅ 급여계산 실행: 체크한 사원(salySpecIdList)만 계산
    @PostMapping("/insa/saly/calc/run")
    public Map<String, Object> run(@RequestBody Map<String, Object> body,
                                   HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        String salyLedgId = (String) body.get("salyLedgId");
        Number grpNoNum   = (Number) body.get("grpNo");

        @SuppressWarnings("unchecked")
        List<String> salySpecIdList = (List<String>) body.get("salySpecIdList");

        if (salyLedgId == null || salyLedgId.isBlank()) {
            res.put("result", "FAIL");
            res.put("message", "급여대장ID가 없습니다.");
            return res;
        }
        if (grpNoNum == null) {
            res.put("result", "FAIL");
            res.put("message", "급여계산그룹번호(grpNo)가 없습니다.");
            return res;
        }
        if (salySpecIdList == null || salySpecIdList.isEmpty()) {
            res.put("result", "FAIL");
            res.put("message", "급여계산을 적용할 사원을 먼저 체크하세요.");
            return res;
        }

        Long grpNo = grpNoNum.longValue();

        try {
            Map<String, Object> data = salyCalcService.previewSalyLedg(
                    salyLedgId,
                    grpNo,
                    salySpecIdList,
                    login.getVendId(),
                    login.getEmpId()
            );
            res.put("result", "SUCCESS");
            res.put("data", data);
        } catch (Exception e) {
            log.error("급여 계산 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }

    
    // ✅ 저장: 미리보기 결과를 DB에 반영
    @PostMapping("/insa/saly/calc/save")
    public Map<String, Object> save(@RequestBody Map<String, Object> body,
                                    HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        String salyLedgId = (String) body.get("salyLedgId");
        Number grpNoNum   = (Number) body.get("grpNo");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> previewList = (List<Map<String, Object>>) body.get("previewList");

        if (salyLedgId == null || salyLedgId.isBlank()) {
            res.put("result", "FAIL");
            res.put("message", "급여대장ID가 없습니다.");
            return res;
        }
        if (grpNoNum == null) {
            res.put("result", "FAIL");
            res.put("message", "급여계산그룹번호(grpNo)가 없습니다.");
            return res;
        }
        if (previewList == null || previewList.isEmpty()) {
            res.put("result", "FAIL");
            res.put("message", "저장할 계산 결과(previewList)가 없습니다.");
            return res;
        }

        Long grpNo = grpNoNum.longValue();

        try {
            salyCalcService.savePreviewResult(
                    salyLedgId,
                    grpNo,
                    previewList,
                    login.getVendId(),
                    login.getEmpId()
            );
            res.put("result", "SUCCESS");
        } catch (Exception e) {
            log.error("급여 계산 저장 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }

@GetMapping("/insa/saly/calc/items")
    public List<SalySpecItemVO> items(@RequestParam("salySpecId") String salySpecId,
                                      @RequestParam("grpNo") Long grpNo,
                                      HttpSession session) {

        SessionDto login = getLogin(session);
        if (login == null) return List.of();

        return salyCalcService.getSalySpecItems(salySpecId, grpNo, login.getVendId());
    }

    // ✅ 급여계산그룹 저장(1건)
    @PostMapping("/insa/saly/calc/grp/save")
    public Map<String, Object> saveGrp(@RequestBody Map<String, Object> body,
                                       HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        Number grpNoNum = (Number) body.get("grpNo");
        String grpNm    = (String) body.get("grpNm");

        @SuppressWarnings("unchecked")
        List<String> itemIds = (List<String>) body.get("itemIds");

        Long grpNo = (grpNoNum == null ? null : grpNoNum.longValue());

        try {
            Long savedGrpNo = salyCalcService.saveCalcGroup(
                    login.getVendId(),
                    login.getEmpId(),
                    grpNo,
                    grpNm,
                    itemIds
            );
            res.put("result", "SUCCESS");
            res.put("grpNo", savedGrpNo);
        } catch (Exception e) {
            log.error("급여계산그룹 저장 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }

    // ✅✅✅ 급여계산그룹 한번에 저장(wkTy 방식) - created/updated/deleted
    @PostMapping("/insa/saly/calc/grp/saveAll")
    public Map<String, Object> saveAll(@RequestBody Map<String, Object> body,
                                       HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> createdRows = (List<Map<String, Object>>) body.get("createdRows");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> updatedRows = (List<Map<String, Object>>) body.get("updatedRows");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> deletedRows = (List<Map<String, Object>>) body.get("deletedRows");

        try {
            salyCalcService.saveCalcGroupAll(
                    login.getVendId(),
                    login.getEmpId(),
                    createdRows,
                    updatedRows,
                    deletedRows
            );
            res.put("result", "SUCCESS");
        } catch (Exception e) {
            log.error("급여계산그룹 saveAll 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }

    // ✅ 급여계산그룹 삭제(1건)
    @PostMapping("/insa/saly/calc/grp/delete")
    public Map<String, Object> deleteGrp(@RequestBody Map<String, Object> body,
                                         HttpSession session) {

        Map<String, Object> res = new HashMap<>();

        SessionDto login = getLogin(session);
        if (login == null) {
            res.put("result", "FAIL");
            res.put("message", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return res;
        }

        Number grpNoNum = (Number) body.get("grpNo");
        if (grpNoNum == null) {
            res.put("result", "FAIL");
            res.put("message", "삭제할 grpNo가 없습니다.");
            return res;
        }

        try {
            salyCalcService.deleteCalcGroup(login.getVendId(), grpNoNum.longValue());
            res.put("result", "SUCCESS");
        } catch (Exception e) {
            log.error("급여계산그룹 삭제 오류", e);
            res.put("result", "FAIL");
            res.put("message", e.getMessage());
        }

        return res;
    }
    @GetMapping("/insa/saly/calc/groupItems")
    @ResponseBody
    public Map<String, Object> getGroupItems(@RequestParam("grpNo") Long grpNo,
                                            HttpSession session) {

        SessionDto login = getLogin(session);
        Map<String, Object> res = new HashMap<>();

        if (login == null) {
            res.put("allowList", List.of());
            res.put("ducList", List.of());
            return res;
        }

        String vendId = login.getVendId();

        // ✅ allowList / ducList를 각각 분리해서 내려주기
        // (service.getAllowDucList는 수당+공제 합쳐서 주는 메서드라 list가 섞일 수 있음)
        res.put("allowList", salyCalcService.getAllowDucList(vendId, grpNo).stream()
                .filter(vo -> vo.getAllowId() != null && !vo.getAllowId().isBlank())
                .toList());

        res.put("ducList", salyCalcService.getAllowDucList(vendId, grpNo).stream()
                .filter(vo -> vo.getDucId() != null && !vo.getDucId().isBlank())
                .toList());

        return res;
    }

}
