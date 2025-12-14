package store.yd2team.insa.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.SalyCalcMapper;
import store.yd2team.insa.service.*;

@Service
@RequiredArgsConstructor
public class SalyCalcServiceImpl implements SalyCalcService {

    private final SalyCalcMapper salyCalcMapper;

    @Override
    public List<SalySpecCalcViewVO> getSalySpecCalcList(String salyLedgId, String vendId) {
        return salyCalcMapper.selectSalySpecCalcList(salyLedgId, vendId);
    }

    @Override
    public List<CalGrpVO> getCalcGroupList(String vendId) {
        return salyCalcMapper.selectCalcGroupList(vendId);
    }

    @Override
    public List<AllowDucVO> getAllowDucList(String vendId, Long grpNo) {
        List<AllowDucVO> a = salyCalcMapper.selectAllowListForCalc(vendId, grpNo);
        List<AllowDucVO> d = salyCalcMapper.selectDucListForCalc(vendId, grpNo);
        List<AllowDucVO> all = new ArrayList<>();
        if (a != null) all.addAll(a);
        if (d != null) all.addAll(d);
        return all;
    }

    @Override
    public List<SalySpecItemVO> getSalySpecItems(String salySpecId, Long grpNo, String vendId) {
        // vendId는 조인에 안 쓰지만(현재 쿼리 기준), Controller 시그니처 유지용
        return salyCalcMapper.selectSalySpecItems(salySpecId, grpNo);
    }

    /**
     * ✅ 급여계산 실행
     * - salySpecIdList 선택된 것만
     * - 프로시저 결과를 tb_saly_spec_item 저장
     * - tb_saly_spec 합계(pay_amt/tt_duc_amt/act_pay_amt) 갱신
     */
    @Override
    @Transactional
    public void calculateSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                                  String vendId, String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank()) throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");
        if (salySpecIdList == null || salySpecIdList.isEmpty()) throw new IllegalArgumentException("salySpecIdList is empty");

        for (String salySpecId : salySpecIdList) {
            if (salySpecId == null || salySpecId.isBlank()) continue;

            // 1) salySpecId -> empId
            String empId = salyCalcMapper.selectEmpIdBySpecId(salySpecId);
            if (empId == null || empId.isBlank()) {
                throw new IllegalStateException("empId not found for salySpecId=" + salySpecId);
            }

            // 2) 프로시저 호출
            Map<String, Object> p = new HashMap<>();
            p.put("p_saly_ledg_id", salyLedgId);
            p.put("p_emp_id", empId);
            p.put("p_grp_no", grpNo);
            p.put("o_result", null);

            List<PayItemRowVO> items = salyCalcMapper.callPrcCalcSalyItems(p);
            if (items == null) items = List.of();

            // 3) 기존 항목 삭제(동일 spec + grp)
            salyCalcMapper.deleteSpecItemsBySpecAndGrp(salySpecId, grpNo);

            // 4) insert 준비
            List<Map<String, Object>> insertList = new ArrayList<>();
         // ✅ Long 기준으로 변경
            long payAmt = 0L;
            long ttDucAmt = 0L;

            for (PayItemRowVO it : items) {
                if (it == null) continue;

                String itemTy = it.getItemTy();
                long amt = (it.getAmt() == null ? 0L : it.getAmt());

                if ("A".equalsIgnoreCase(itemTy)) {
                    payAmt += amt;
                } else if ("D".equalsIgnoreCase(itemTy)) {
                    ttDucAmt += amt;
                }

                Map<String, Object> row = new HashMap<>();
                row.put("salySpecId", salySpecId);
                row.put("grpNo", grpNo);
                row.put("itemTy", it.getItemTy());
                row.put("itemId", it.getItemId());
                row.put("itemNm", it.getItemNm());
                row.put("amt", amt);              // ✅ Long 그대로
                row.put("creaBy", loginEmpId);
                insertList.add(row);
            }


            if (!insertList.isEmpty()) {
                Map<String, Object> ins = new HashMap<>();
                ins.put("list", insertList);
                salyCalcMapper.insertSpecItems(ins);
            }

            // 5) 합계 갱신
            long actPayAmt = payAmt - ttDucAmt;


            Map<String, Object> upd = new HashMap<>();
            upd.put("salySpecId", salySpecId);
            upd.put("payAmt", payAmt);
            upd.put("ttDucAmt", ttDucAmt);
            upd.put("actPayAmt", actPayAmt);
            upd.put("updtBy", loginEmpId);

            salyCalcMapper.updateSalySpecTotals(upd);
        }
    }

    /**
     * ✅ 단건 저장 (Controller에 이미 존재하므로 유지)
     * - grpNo null이면 신규 생성
     * - 매핑테이블은 "삭제 후 재삽입" 방식
     */
    @Override
    @Transactional
    public Long saveCalcGroup(String vendId, String empId, Long grpNo, String grpNm, List<String> itemIds) {

        if (vendId == null || vendId.isBlank()) throw new IllegalArgumentException("vendId is required");
        if (empId == null || empId.isBlank()) throw new IllegalArgumentException("empId is required");
        if (grpNm == null || grpNm.isBlank()) throw new IllegalArgumentException("grpNm is required");
        if (itemIds == null || itemIds.isEmpty()) throw new IllegalArgumentException("itemIds is empty");

        Long savedGrpNo = grpNo;

        if (savedGrpNo == null) {
            savedGrpNo = salyCalcMapper.selectNextCalcGrpNo();
            salyCalcMapper.insertCalcGroup(savedGrpNo, vendId, grpNm, empId);
        } else {
            salyCalcMapper.updateCalcGroup(vendId, savedGrpNo, grpNm, empId);
        }

        // 매핑 갱신
        salyCalcMapper.deleteGrpItemsByGrpNo(vendId, savedGrpNo);
        List<Map<String, Object>> list = buildGrpItemRows(vendId, savedGrpNo, empId, itemIds);

        Map<String, Object> p = new HashMap<>();
        p.put("list", list);
        salyCalcMapper.insertGrpItems(p);

        return savedGrpNo;
    }

    /**
     * ✅ saveAll (wkTy 방식)
     * createdRows: [{grpNm, itemIds[]}]
     * updatedRows: [{grpNo, grpNm, itemIds[]}]
     * deletedRows: [{grpNo}]
     */
    @Override
    @Transactional
    public void saveCalcGroupAll(String vendId, String empId,
                                 List<Map<String, Object>> createdRows,
                                 List<Map<String, Object>> updatedRows,
                                 List<Map<String, Object>> deletedRows) {

        if (vendId == null || vendId.isBlank()) throw new IllegalArgumentException("vendId is required");
        if (empId == null || empId.isBlank()) throw new IllegalArgumentException("empId is required");

        // 1) 삭제
        if (deletedRows != null) {
            for (Map<String, Object> r : deletedRows) {
                Long grpNo = toLong(r.get("grpNo"));
                if (grpNo == null) continue;
                salyCalcMapper.deleteGrpItemsByGrpNo(vendId, grpNo);
                salyCalcMapper.deleteCalcGroup(vendId, grpNo);
            }
        }

        // 2) 수정
        if (updatedRows != null) {
            for (Map<String, Object> r : updatedRows) {
                Long grpNo = toLong(r.get("grpNo"));
                String grpNm = toStr(r.get("grpNm"));
                List<String> itemIds = toStrList(r.get("itemIds"));
                if (grpNo == null) continue;
                if (grpNm == null || grpNm.isBlank()) throw new IllegalArgumentException("grpNm is required");
                if (itemIds == null || itemIds.isEmpty()) throw new IllegalArgumentException("itemIds is empty");

                salyCalcMapper.updateCalcGroup(vendId, grpNo, grpNm, empId);

                salyCalcMapper.deleteGrpItemsByGrpNo(vendId, grpNo);
                List<Map<String, Object>> list = buildGrpItemRows(vendId, grpNo, empId, itemIds);

                Map<String, Object> p = new HashMap<>();
                p.put("list", list);
                salyCalcMapper.insertGrpItems(p);
            }
        }

        // 3) 생성
        if (createdRows != null) {
            for (Map<String, Object> r : createdRows) {
                String grpNm = toStr(r.get("grpNm"));
                List<String> itemIds = toStrList(r.get("itemIds"));
                if (grpNm == null || grpNm.isBlank()) throw new IllegalArgumentException("grpNm is required");
                if (itemIds == null || itemIds.isEmpty()) throw new IllegalArgumentException("itemIds is empty");

                Long grpNo = salyCalcMapper.selectNextCalcGrpNo();
                salyCalcMapper.insertCalcGroup(grpNo, vendId, grpNm, empId);

                salyCalcMapper.deleteGrpItemsByGrpNo(vendId, grpNo);
                List<Map<String, Object>> list = buildGrpItemRows(vendId, grpNo, empId, itemIds);

                Map<String, Object> p = new HashMap<>();
                p.put("list", list);
                salyCalcMapper.insertGrpItems(p);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCalcGroup(String vendId, Long grpNo) {
        if (vendId == null || vendId.isBlank()) throw new IllegalArgumentException("vendId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");

        salyCalcMapper.deleteGrpItemsByGrpNo(vendId, grpNo);
        salyCalcMapper.deleteCalcGroup(vendId, grpNo);
    }

    // ----------------- 내부 유틸 -----------------

    private List<Map<String, Object>> buildGrpItemRows(String vendId, Long grpNo, String creaBy, List<String> itemIds) {
        // itemIds는 A/D 혼합이라서, 여기서 A/D 판별이 필요함.
        // ✅ 판별 규칙: ALW로 시작하면 A, DUC로 시작하면 D
        // (지웅님 AllowDucMapper에서 신규ID 규칙이 ALW/DUC였음)
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String id : itemIds) {
            if (id == null || id.isBlank()) continue;

            String itemTy;
            String up = id.toUpperCase(Locale.ROOT);
            if (up.startsWith("ALW")) itemTy = "A";
            else if (up.startsWith("DUC")) itemTy = "D";
            else {
                // 혹시 규칙 밖이면 안전하게 예외로 터트려서 데이터 꼬임 방지
                throw new IllegalArgumentException("Unknown itemId prefix: " + id + " (expected ALW*/DUC*)");
            }

            Map<String, Object> m = new HashMap<>();
            m.put("vendId", vendId);
            m.put("grpNo", grpNo);
            m.put("itemTy", itemTy);
            m.put("itemId", id);
            m.put("creaBy", creaBy);
            rows.add(m);
        }
        return rows;
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e) { return null; }
    }

    private String toStr(Object o) {
        return (o == null ? null : String.valueOf(o));
    }

    @SuppressWarnings("unchecked")
    private List<String> toStrList(Object o) {
        if (o == null) return null;
        if (o instanceof List<?> list) {
            return list.stream().map(x -> x == null ? null : String.valueOf(x)).collect(Collectors.toList());
        }
        return null;
    }
}
