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
        return salyCalcMapper.selectSalySpecItems(salySpecId, grpNo);
    }

    /**
     * ✅ 급여계산 실행
     * - salySpecIdList 선택된 것만
     * - 프로시저 결과를 tb_saly_spec_item 저장
     * - 저장 순서: 수당(A) disp_no 순 → 공제(D) disp_no 순
     * - tb_saly_spec 합계(pay_amt/tt_duc_amt/act_pay_amt) 갱신
     *
     * ✅ FIX 핵심(이번 ORA-00001):
     * - 프로시저 결과에 같은 (item_ty + item_id)가 중복으로 내려올 수 있음
     * - PK가 그 조합을 포함하면 INSERT 때 ORA-00001 발생
     * - 그래서 insert 전에 (item_ty + item_id) 기준으로 "합산/중복제거" 후 저장
     */
    @Override
    @Transactional
    public void calculateSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                                  String vendId, String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank()) throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");
        if (salySpecIdList == null || salySpecIdList.isEmpty()) throw new IllegalArgumentException("salySpecIdList is empty");

        // ✅ 그룹에 매핑된 항목들의 dispNo 맵(정렬 기준)
        Map<String, Long> allowDispMap = new HashMap<>();
        Map<String, Long> ducDispMap = new HashMap<>();

        List<AllowDucVO> allowList = salyCalcMapper.selectAllowListForCalc(vendId, grpNo);
        if (allowList != null) {
            for (AllowDucVO vo : allowList) {
                if (vo == null) continue;
                if (vo.getAllowId() == null) continue;
                allowDispMap.put(vo.getAllowId(), vo.getDispNo() == null ? 999999L : vo.getDispNo());
            }
        }

        List<AllowDucVO> ducList = salyCalcMapper.selectDucListForCalc(vendId, grpNo);
        if (ducList != null) {
            for (AllowDucVO vo : ducList) {
                if (vo == null) continue;
                if (vo.getDucId() == null) continue;
                ducDispMap.put(vo.getDucId(), vo.getDispNo() == null ? 999999L : vo.getDispNo());
            }
        }

        for (String salySpecId : salySpecIdList) {
            if (salySpecId == null || salySpecId.isBlank()) continue;

            // 1) salySpecId -> empId
            String empId = salyCalcMapper.selectEmpIdBySpecId(salySpecId);
            if (empId == null || empId.isBlank()) {
                throw new IllegalStateException("empId not found for salySpecId=" + salySpecId);
            }

            // 2) 프로시저 호출 (OUT CURSOR는 p.get("o_result")로 받는다)
            Map<String, Object> p = new HashMap<>();
            p.put("p_saly_ledg_id", salyLedgId);
            p.put("p_emp_id", empId);
            p.put("p_grp_no", grpNo);
            p.put("o_result", null);

            salyCalcMapper.callPrcCalcSalyItems(p);

            @SuppressWarnings("unchecked")
            List<PayItemRowVO> items = (List<PayItemRowVO>) p.get("o_result");
            if (items == null) items = List.of();

            // ============================================================
            // ✅ FIX: (itemTy + itemId) 기준으로 중복 제거/합산
            // - 같은 항목이 2번 나오면 amt를 합쳐서 1건으로 만든다
            // - 이렇게 하면 PK 중복으로 ORA-00001 안 남
            // ============================================================
            LinkedHashMap<String, PayItemRowVO> merged = new LinkedHashMap<>();
            for (PayItemRowVO it : items) {
                if (it == null) continue;

                String ty = it.getItemTy() == null ? "" : it.getItemTy().trim().toUpperCase(Locale.ROOT);
                String id = it.getItemId() == null ? "" : it.getItemId().trim();

                if (ty.isBlank() || id.isBlank()) continue; // PK 구성일 가능성 높아서 빈 값은 저장 제외

                String key = ty + "|" + id;

                PayItemRowVO exist = merged.get(key);
                if (exist == null) {
                    PayItemRowVO one = new PayItemRowVO();
                    one.setItemTy(ty);
                    one.setItemId(id);
                    one.setItemNm(it.getItemNm());
                    one.setAmt(it.getAmt() == null ? 0L : it.getAmt());
                    merged.put(key, one);
                } else {
                    long a = (exist.getAmt() == null ? 0L : exist.getAmt());
                    long b = (it.getAmt() == null ? 0L : it.getAmt());
                    exist.setAmt(a + b);

                    // 이름이 비어있던 경우 보정
                    if ((exist.getItemNm() == null || exist.getItemNm().isBlank())
                            && it.getItemNm() != null && !it.getItemNm().isBlank()) {
                        exist.setItemNm(it.getItemNm());
                    }
                }
            }

            List<PayItemRowVO> mergedList = new ArrayList<>(merged.values());

            // ✅ A 먼저 → dispNo 기준 정렬 → 그 다음 D dispNo
            mergedList.sort((x, y) -> {
                if (x == null && y == null) return 0;
                if (x == null) return 1;
                if (y == null) return -1;

                String xt = x.getItemTy() == null ? "" : x.getItemTy().toUpperCase(Locale.ROOT);
                String yt = y.getItemTy() == null ? "" : y.getItemTy().toUpperCase(Locale.ROOT);

                int xOrder = "A".equals(xt) ? 1 : ("D".equals(xt) ? 2 : 9);
                int yOrder = "A".equals(yt) ? 1 : ("D".equals(yt) ? 2 : 9);
                if (xOrder != yOrder) return Integer.compare(xOrder, yOrder);

                long xDisp = 999999L;
                long yDisp = 999999L;

                if ("A".equals(xt)) xDisp = allowDispMap.getOrDefault(x.getItemId(), 999999L);
                if ("D".equals(xt)) xDisp = ducDispMap.getOrDefault(x.getItemId(), 999999L);

                if ("A".equals(yt)) yDisp = allowDispMap.getOrDefault(y.getItemId(), 999999L);
                if ("D".equals(yt)) yDisp = ducDispMap.getOrDefault(y.getItemId(), 999999L);

                int c = Long.compare(xDisp, yDisp);
                if (c != 0) return c;

                String xid = x.getItemId() == null ? "" : x.getItemId();
                String yid = y.getItemId() == null ? "" : y.getItemId();
                return xid.compareTo(yid);
            });

            // 3) 기존 항목 삭제 (지웅님이 이미 바꾼 spec 기준 삭제 유지)
            salyCalcMapper.deleteSpecItemsBySpec(salySpecId);

            // 4) insert 준비 + 합계 계산
            List<Map<String, Object>> insertList = new ArrayList<>();

            long payAmt = 0L;
            long ttDucAmt = 0L;

            for (PayItemRowVO it : mergedList) {
                if (it == null) continue;

                String itemTy = it.getItemTy();
                long amt = (it.getAmt() == null ? 0L : it.getAmt());

                if ("A".equalsIgnoreCase(itemTy)) payAmt += amt;
                else if ("D".equalsIgnoreCase(itemTy)) ttDucAmt += amt;

                Map<String, Object> row = new HashMap<>();
                row.put("salySpecId", salySpecId);
                row.put("grpNo", grpNo);
                row.put("itemTy", it.getItemTy());
                row.put("itemId", it.getItemId());
                row.put("itemNm", it.getItemNm());
                row.put("amt", amt);
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

    // ----------------- 기존 그룹 저장/삭제 로직은 그대로 -----------------

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

        salyCalcMapper.deleteGrpItemsByGrpNo(vendId, savedGrpNo);
        List<Map<String, Object>> list = buildGrpItemRows(vendId, savedGrpNo, empId, itemIds);

        Map<String, Object> p = new HashMap<>();
        p.put("list", list);
        salyCalcMapper.insertGrpItems(p);

        return savedGrpNo;
    }

    @Override
    @Transactional
    public void saveCalcGroupAll(String vendId, String empId,
                                 List<Map<String, Object>> createdRows,
                                 List<Map<String, Object>> updatedRows,
                                 List<Map<String, Object>> deletedRows) {

        if (vendId == null || vendId.isBlank()) throw new IllegalArgumentException("vendId is required");
        if (empId == null || empId.isBlank()) throw new IllegalArgumentException("empId is required");

        if (deletedRows != null) {
            for (Map<String, Object> r : deletedRows) {
                Long grpNo = toLong(r.get("grpNo"));
                if (grpNo == null) continue;
                salyCalcMapper.deleteGrpItemsByGrpNo(vendId, grpNo);
                salyCalcMapper.deleteCalcGroup(vendId, grpNo);
            }
        }

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
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String id : itemIds) {
            if (id == null || id.isBlank()) continue;

            String itemTy;
            String up = id.toUpperCase(Locale.ROOT);
            if (up.startsWith("ALW")) itemTy = "A";
            else if (up.startsWith("DUC")) itemTy = "D";
            else throw new IllegalArgumentException("Unknown itemId prefix: " + id + " (expected ALW*/DUC*)");

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
