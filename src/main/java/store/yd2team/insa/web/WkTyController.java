package store.yd2team.insa.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.service.DayVO;
import store.yd2team.insa.service.EdcService;
import store.yd2team.insa.service.HldyVO;
import store.yd2team.insa.service.HldyWkBasiVO;
import store.yd2team.insa.service.WkTyService;

@Controller
@RequiredArgsConstructor
public class WkTyController {

    private final WkTyService wkTyService;
    private final EdcService edcService;

    /** 교육/인사 공통 입력 옵션 (직급/직책/고용형태/재직상태/부서 등) */
    @GetMapping(value = "/insa/edc/inputOption", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Map<String, Object> getInputOption() {
        return edcService.getInputOption();
    }

    // ================== 화면 열기 ==================
    @GetMapping("/wkTy")
    public String wkTyPage() {
        return "insa/wkTy";
    }

    // ================== 휴일 기준 쪽 ==================

    // 법정 공휴일 목록
    @GetMapping("/api/hldy/legal/list")
    @ResponseBody
    public List<HldyVO> getLegalHldyList() {
        return wkTyService.selectLegalHlDyList();
    }

    // 회사 지정 공휴일 목록
    @GetMapping("/api/hldy/company/list")
    @ResponseBody
    public List<HldyVO> getCompanyHldyList() {
        String vendId = LoginSession.getVendId();
        return wkTyService.selectCompanyHlDyList(vendId);
    }

    // 회사 지정 공휴일 저장
    @PostMapping("/api/hldy/company/save")
    @ResponseBody
    public Map<String, Object> saveCompanyHldy(
            @RequestBody Map<String, List<HldyVO>> body) {

        String empId  = LoginSession.getEmpId();
        String vendId = LoginSession.getVendId();

        List<HldyVO> createdRows = body.get("createdRows");
        List<HldyVO> updatedRows = body.get("updatedRows");
        List<HldyVO> deletedRows = body.get("deletedRows");

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

        if (updatedRows != null) {
            for (HldyVO vo : updatedRows) {
                if (vo.getHldyNm() != null) {
                    vo.setHldyNm(vo.getHldyNm().trim());
                }
                vo.setUpdtBy(empId);
                vo.setVendId(vendId);
                wkTyService.updateHlDy(vo);
            }
        }

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

    // ================== 근무형태 기준 쪽 ==================

    /** 기준 목록 (모달 그리드용) */
    @GetMapping("/insa/hldyWkBasi/list")
    @ResponseBody
    public List<Map<String, Object>> hldyWkBasiList(
            @RequestParam(value = "basiNm", required = false) String basiNm,
            @RequestParam(value = "deptId", required = false) String deptId) {

        HldyWkBasiVO search = new HldyWkBasiVO();
        search.setBasiNm(basiNm);
        search.setDeptId(deptId);

        List<HldyWkBasiVO> list = wkTyService.getHldyWkBasiList(search);

        // JS에서 substring(11,16) 쓰고 있어서 "yyyy-MM-dd'T'HH:mm:ss" 형태로 맞춰줌
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return list.stream().map(vo -> {
            Map<String, Object> m = new HashMap<>();
            m.put("basiNo", vo.getBasiNo());
            m.put("basiNm", vo.getBasiNm());
            m.put("deptNm", vo.getDeptNm());
            m.put("atdcTm", vo.getAtdcTm() != null ? fmt.format(vo.getAtdcTm()) : null);
            m.put("afwkTm", vo.getAfwkTm() != null ? fmt.format(vo.getAfwkTm()) : null);
            return m;
        }).toList();
    }

    /** 기준 상세 1건 (우측 폼 채우기용) */
    @GetMapping("/insa/hldyWkBasi/detail")
    @ResponseBody
    public Map<String, Object> hldyWkBasiDetail(@RequestParam("basiNo") Long basiNo) {

        HldyWkBasiVO vo = wkTyService.getHldyWkBasi(basiNo);
        if (vo == null) {
            return Collections.emptyMap();
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Map<String, Object> m = new HashMap<>();
        m.put("basiNo", vo.getBasiNo());
        m.put("basiNm", vo.getBasiNm());
        m.put("deptId", vo.getDeptId());
        m.put("atdcTm", vo.getAtdcTm() != null ? fmt.format(vo.getAtdcTm()) : null);
        m.put("afwkTm", vo.getAfwkTm() != null ? fmt.format(vo.getAfwkTm()) : null);
        return m;
    }

    /** 기준별 요일 목록 (선택 요일) */
    @GetMapping("/insa/hldyWkBasi/day/list")
    @ResponseBody
    public List<DayVO> hldyWkBasiDayList(@RequestParam("basiNo") Long basiNo) {
        return wkTyService.getDayListByBasiNo(basiNo);
    }

    /** 기준 저장 (추가/수정 + 요일까지) */
    @PostMapping("/insa/hldyWkBasi/save")
    @ResponseBody
    public Map<String, Object> saveHldyWkBasi(@RequestBody Map<String, Object> body)
            throws ParseException {

        Object basiNoObj = body.get("basiNo");
        Long basiNo = null;
        if (basiNoObj != null) {
            if (basiNoObj instanceof Number num) basiNo = num.longValue();
            else basiNo = Long.valueOf(basiNoObj.toString());
        }

        String deptId   = (String) body.get("deptId");
        String basiNm   = (String) body.get("basiNm");
        String atdcStr  = (String) body.get("atdcTm");
        String afwkStr  = (String) body.get("afwkTm");

        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");
        Date atdcTm = null;
        Date afwkTm = null;

        if (StringUtils.hasText(atdcStr)) {
            atdcTm = timeFmt.parse(atdcStr);
        }
        if (StringUtils.hasText(afwkStr)) {
            afwkTm = timeFmt.parse(afwkStr);
        }

        HldyWkBasiVO vo = new HldyWkBasiVO();
        vo.setBasiNo(basiNo);
        vo.setDeptId(deptId);
        vo.setBasiNm(basiNm);
        vo.setAtdcTm(atdcTm);
        vo.setAfwkTm(afwkTm);
        // hldyTy / wkDe / hldyDe 제거됨

        @SuppressWarnings("unchecked")
        List<String> dayList =
                (List<String>) body.getOrDefault("dayList", Collections.emptyList());

        Long savedNo = wkTyService.saveHldyWkBasi(vo, dayList);

        Map<String, Object> res = new HashMap<>();
        res.put("result", "SUCCESS");
        res.put("basiNo", savedNo);
        return res;
    }

    /** 기준 삭제 (기준 + 요일 같이 삭제) */
    @DeleteMapping("/insa/hldyWkBasi/delete")
    @ResponseBody
    public Map<String, Object> deleteHldyWkBasi(@RequestParam("basiNo") Long basiNo) {

        wkTyService.deleteHldyWkBasi(basiNo);

        Map<String, Object> res = new HashMap<>();
        res.put("result", "SUCCESS");
        return res;
    }
}
