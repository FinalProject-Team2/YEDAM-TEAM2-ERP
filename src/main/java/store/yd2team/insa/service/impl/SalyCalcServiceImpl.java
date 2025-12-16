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

    /**
     * ✅ 미리보기(확인): DB 저장 없이 계산 결과만 반환
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> previewSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                                               String vendId, String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank()) throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");
        if (salySpecIdList == null || salySpecIdList.isEmpty()) throw new IllegalArgumentException("salySpecIdList is empty");

        // 표시번호(정렬용) 맵
        Map<String, Long> allowDispMap = buildAllowDispMap(vendId, grpNo);
        Map<String, Long> ducDispMap   = buildDucDispMap(vendId, grpNo);

        Map<String, Object> result = new HashMap<>();

        for (String salySpecId : salySpecIdList) {
            if (salySpecId == null || salySpecId.isBlank()) continue;

            String empId = salyCalcMapper.selectEmpIdBySpecId(salySpecId);
            if (empId == null || empId.isBlank()) {
                throw new IllegalStateException("empId not found for salySpecId=" + salySpecId);
            }

            CalcResult one = calcOnce(salyLedgId, empId, grpNo, allowDispMap, ducDispMap);

            Map<String, Object> payload = new HashMap<>();
            payload.put("items", one.items);
            payload.put("payAmt", one.payAmt);
            payload.put("ttDucAmt", one.ttDucAmt);
            payload.put("actPayAmt", one.actPayAmt);

            result.put(salySpecId, payload);
        }

        return result;
    }

    /**
     * ✅ 저장: 미리보기 결과를 DB에 반영
     * previewList: [{ salySpecId: "...", items: [ {itemTy,itemId,itemNm,amt}, ... ] }, ...]
     */
    @Override
    @Transactional
    public void savePreviewResult(String salyLedgId, Long grpNo, List<Map<String, Object>> previewList,
                                  String vendId, String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank()) throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");
        if (previewList == null || previewList.isEmpty()) throw new IllegalArgumentException("previewList is empty");

        // 표시번호(정렬용) 맵 (혹시라도 정렬이 깨져서 와도 서버에서 다시 정렬/중복제거)
        Map<String, Long> allowDispMap = buildAllowDispMap(vendId, grpNo);
        Map<String, Long> ducDispMap   = buildDucDispMap(vendId, grpNo);

        for (Map<String, Object> one : previewList) {
            if (one == null) continue;

            String salySpecId = (String) one.get("salySpecId");
            if (salySpecId == null || salySpecId.isBlank()) continue;

            // ====== [수정] LinkedHashMap(List<Map>)로 들어오는 items를 VO로 변환 ======
            List<PayItemRowVO> items = coercePayItemRows(one.get("items"));
            if (items == null) items = List.of();
            // =====================================================================

            CalcResult cr = normalizeItems(items, allowDispMap, ducDispMap);

            // ✅ 1) 기존 spec+grp 삭제
            salyCalcMapper.deleteSpecItemsBySpecAndGrp(salySpecId, grpNo);

            // ✅ 2) 이번에 넣을 item_id만 추가 삭제 → 다른 grp에 남아있던 동일 item_id 충돌 제거
            List<String> itemIdList = cr.items.stream()
                    .map(PayItemRowVO::getItemId)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());

            if (!itemIdList.isEmpty()) {
                Map<String, Object> del = new HashMap<>();
                del.put("salySpecId", salySpecId);
                del.put("itemIdList", itemIdList);
                salyCalcMapper.deleteSpecItemsBySpecAndItemIds(del);
            }

            // ✅ 3) insert 준비
            List<Map<String, Object>> insertList = new ArrayList<>();
            for (PayItemRowVO it : cr.items) {
                Long itemNo = salyCalcMapper.selectNextSpecItemNo();

                Map<String, Object> row = new HashMap<>();
                row.put("salySpecId", salySpecId);
                row.put("grpNo", grpNo);
                row.put("itemNo", itemNo);
                row.put("itemTy", it.getItemTy());
                row.put("itemId", it.getItemId());
                row.put("itemNm", it.getItemNm());
                row.put("amt", it.getAmt());
                row.put("creaBy", loginEmpId);
                row.put("updtBy", loginEmpId);

                insertList.add(row);
            }

            if (!insertList.isEmpty()) {
                Map<String, Object> ins = new HashMap<>();
                ins.put("list", insertList);
                salyCalcMapper.insertSpecItems(ins);
            }

            // ✅ 4) 합계 업데이트
            Map<String, Object> upd = new HashMap<>();
            upd.put("salySpecId", salySpecId);
            upd.put("payAmt", cr.payAmt);
            upd.put("ttDucAmt", cr.ttDucAmt);
            upd.put("actPayAmt", cr.actPayAmt);
            upd.put("updtBy", loginEmpId);

            salyCalcMapper.updateSalySpecTotals(upd);
        }
    }

    // =========================
    // 내부 유틸(정렬/중복제거/합계)
    // =========================
    private static class CalcResult {
        List<PayItemRowVO> items;
        long payAmt;
        long ttDucAmt;
        long actPayAmt;
        CalcResult(List<PayItemRowVO> items, long payAmt, long ttDucAmt, long actPayAmt) {
            this.items = items;
            this.payAmt = payAmt;
            this.ttDucAmt = ttDucAmt;
            this.actPayAmt = actPayAmt;
        }
    }

    private Map<String, Long> buildAllowDispMap(String vendId, Long grpNo) {
        Map<String, Long> map = new HashMap<>();
        List<AllowDucVO> allowList = salyCalcMapper.selectAllowListForCalc(vendId, grpNo);
        if (allowList != null) {
            for (AllowDucVO vo : allowList) {
                if (vo == null || vo.getAllowId() == null) continue;
                map.put(vo.getAllowId(), vo.getDispNo() == null ? 999999L : vo.getDispNo().longValue());
            }
        }
        return map;
    }

    private Map<String, Long> buildDucDispMap(String vendId, Long grpNo) {
        Map<String, Long> map = new HashMap<>();
        List<AllowDucVO> ducList = salyCalcMapper.selectDucListForCalc(vendId, grpNo);
        if (ducList != null) {
            for (AllowDucVO vo : ducList) {
                if (vo == null || vo.getDucId() == null) continue;
                map.put(vo.getDucId(), vo.getDispNo() == null ? 999999L : vo.getDispNo().longValue());
            }
        }
        return map;
    }

    private CalcResult calcOnce(String salyLedgId, String empId, Long grpNo,
                                Map<String, Long> allowDispMap, Map<String, Long> ducDispMap) {

        Map<String, Object> p = new HashMap<>();
        p.put("p_saly_ledg_id", salyLedgId);
        p.put("p_emp_id", empId);
        p.put("p_grp_no", grpNo);
        p.put("o_result", null);

        salyCalcMapper.callPrcCalcSalyItems(p);

        // ====== [수정] o_result도 List<Map>로 올 수 있어서 VO로 변환 ======
        List<PayItemRowVO> items = coercePayItemRows(p.get("o_result"));
        if (items == null) items = List.of();
        // ====================================================================

        return normalizeItems(items, allowDispMap, ducDispMap);
    }

    private CalcResult normalizeItems(List<PayItemRowVO> items,
                                      Map<String, Long> allowDispMap,
                                      Map<String, Long> ducDispMap) {

        // 1) 정렬
        List<PayItemRowVO> sorted = new ArrayList<>(items);
        sorted.sort((x, y) -> {
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

            if (xDisp != yDisp) return Long.compare(xDisp, yDisp);

            String xi = x.getItemId() == null ? "" : x.getItemId();
            String yi = y.getItemId() == null ? "" : y.getItemId();
            return xi.compareTo(yi);
        });

        // 2) itemId 중복 제거(마지막 값 유지)
        Map<String, PayItemRowVO> dedup = new LinkedHashMap<>();
        for (PayItemRowVO it : sorted) {
            if (it == null) continue;
            String key = (it.getItemId() == null ? "" : it.getItemId());
            if (key.isBlank()) continue;
            dedup.put(key, it);
        }
        List<PayItemRowVO> finalItems = new ArrayList<>(dedup.values());

        // 3) 합계
        long payAmt = 0L;
        long ttDucAmt = 0L;

        for (PayItemRowVO it : finalItems) {
            String itemTy = it.getItemTy();
            long amt = (it.getAmt() == null ? 0L : it.getAmt());

            if ("A".equalsIgnoreCase(itemTy)) payAmt += amt;
            else if ("D".equalsIgnoreCase(itemTy)) ttDucAmt += amt;
        }

        long actPayAmt = payAmt - ttDucAmt;

        return new CalcResult(finalItems, payAmt, ttDucAmt, actPayAmt);
    }

    @Override
    public List<SalySpecItemVO> getSalySpecItems(String salySpecId, Long grpNo, String vendId) {
        return salyCalcMapper.selectSalySpecItems(salySpecId, grpNo);
    }

    @Override
    @Transactional
    public void calculateSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                                  String vendId, String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank()) throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");
        if (salySpecIdList == null || salySpecIdList.isEmpty()) throw new IllegalArgumentException("salySpecIdList is empty");

        Map<String, Long> allowDispMap = new HashMap<>();
        Map<String, Long> ducDispMap = new HashMap<>();

        List<AllowDucVO> allowList = salyCalcMapper.selectAllowListForCalc(vendId, grpNo);
        if (allowList != null) {
            for (AllowDucVO vo : allowList) {
                if (vo == null || vo.getAllowId() == null) continue;
                allowDispMap.put(vo.getAllowId(), vo.getDispNo() == null ? 999999L : vo.getDispNo());
            }
        }

        List<AllowDucVO> ducList = salyCalcMapper.selectDucListForCalc(vendId, grpNo);
        if (ducList != null) {
            for (AllowDucVO vo : ducList) {
                if (vo == null || vo.getDucId() == null) continue;
                ducDispMap.put(vo.getDucId(), vo.getDispNo() == null ? 999999L : vo.getDispNo());
            }
        }

        for (String salySpecId : salySpecIdList) {
            if (salySpecId == null || salySpecId.isBlank()) continue;

            String empId = salyCalcMapper.selectEmpIdBySpecId(salySpecId);
            if (empId == null || empId.isBlank()) {
                throw new IllegalStateException("empId not found for salySpecId=" + salySpecId);
            }

            Map<String, Object> p = new HashMap<>();
            p.put("p_saly_ledg_id", salyLedgId);
            p.put("p_emp_id", empId);
            p.put("p_grp_no", grpNo);
            p.put("o_result", null);

            salyCalcMapper.callPrcCalcSalyItems(p);

            // ====== [수정] 여기 캐스팅도 동일하게 안전 변환 ======
            List<PayItemRowVO> items = coercePayItemRows(p.get("o_result"));
            if (items == null) items = List.of();
            // =======================================================

            // ✅ 1) 먼저 정렬
            List<PayItemRowVO> sorted = new ArrayList<>(items);
            sorted.sort((x, y) -> {
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

            // ✅ 2) (핵심) itemTy+itemId 기준 중복 제거(amt 합산)
            // PK가 item_no가 아니어도 여기서 중복이 사라져서 ORA-00001 방지됨
            Map<String, PayItemRowVO> dedup = new LinkedHashMap<>();
            for (PayItemRowVO it : sorted) {
                if (it == null) continue;
                String ty = it.getItemTy() == null ? "" : it.getItemTy().toUpperCase(Locale.ROOT);
                String id = it.getItemId() == null ? "" : it.getItemId();
                if (id.isBlank()) continue;

                String key = ty + "|" + id;

                PayItemRowVO prev = dedup.get(key);
                if (prev == null) {
                    PayItemRowVO n = new PayItemRowVO();
                    n.setItemTy(it.getItemTy());
                    n.setItemId(it.getItemId());
                    n.setItemNm(it.getItemNm());
                    n.setAmt(it.getAmt() == null ? 0L : it.getAmt());
                    dedup.put(key, n);
                } else {
                    long a = prev.getAmt() == null ? 0L : prev.getAmt();
                    long b = it.getAmt() == null ? 0L : it.getAmt();
                    prev.setAmt(a + b);
                    if ((prev.getItemNm() == null || prev.getItemNm().isBlank()) && it.getItemNm() != null) {
                        prev.setItemNm(it.getItemNm());
                    }
                }
            }
            List<PayItemRowVO> finalItems = new ArrayList<>(dedup.values());

            // ✅ 3) 기존 spec+grp 삭제는 그대로
            salyCalcMapper.deleteSpecItemsBySpecAndGrp(salySpecId, grpNo);

            // ✅ 4) (핵심) 이번에 넣을 item_id만 추가 삭제 → 다른 grp에 남아있던 동일 item_id 충돌 제거
            List<String> itemIdList = finalItems.stream()
                    .map(PayItemRowVO::getItemId)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());

            if (!itemIdList.isEmpty()) {
                Map<String, Object> del = new HashMap<>();
                del.put("salySpecId", salySpecId);
                del.put("itemIdList", itemIdList);
                salyCalcMapper.deleteSpecItemsBySpecAndItemIds(del);
            }

            // ✅ 5) insert 준비 + 합계
            List<Map<String, Object>> insertList = new ArrayList<>();
            long payAmt = 0L;
            long ttDucAmt = 0L;

            for (PayItemRowVO it : finalItems) {
                String itemTy = it.getItemTy();
                long amt = (it.getAmt() == null ? 0L : it.getAmt());

                if ("A".equalsIgnoreCase(itemTy)) payAmt += amt;
                else if ("D".equalsIgnoreCase(itemTy)) ttDucAmt += amt;

                Long itemNo = salyCalcMapper.selectNextSpecItemNo();

                Map<String, Object> row = new HashMap<>();
                row.put("salySpecId", salySpecId);
                row.put("grpNo", grpNo);
                row.put("itemNo", itemNo);
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

    // ======================================================================
    // [추가] LinkedHashMap -> PayItemRowVO 안전 변환 유틸 (기존 로직 영향 없음)
    // ======================================================================

    /**
     * raw가 List<PayItemRowVO>로 오든, List<LinkedHashMap>로 오든, 섞여 오든
     * PayItemRowVO 리스트로 안전하게 변환합니다.
     */
    private List<PayItemRowVO> coercePayItemRows(Object raw) {
        if (raw == null) return null;

        if (!(raw instanceof List<?> list)) {
            return null;
        }

        List<PayItemRowVO> out = new ArrayList<>();
        for (Object e : list) {
            if (e == null) continue;

            if (e instanceof PayItemRowVO vo) {
                out.add(vo);
                continue;
            }

            if (e instanceof Map<?, ?> m) {
                PayItemRowVO vo = mapToPayItemRowVO(m);
                if (vo != null) out.add(vo);
                continue;
            }

            // 그 외 타입은 무시
        }
        return out;
    }

    /**
     * Map 키는 itemTy/itemId/itemNm/amt 를 기본으로 보고,
     * 혹시 DB alias/프론트 키가 다를 수 있어 소문자/대문자도 흡수합니다.
     */
    private PayItemRowVO mapToPayItemRowVO(Map<?, ?> m) {
        if (m == null) return null;

        String itemTy = toStrByKeys(m, "itemTy", "ITEM_TY", "item_ty");
        String itemId = toStrByKeys(m, "itemId", "ITEM_ID", "item_id");
        String itemNm = toStrByKeys(m, "itemNm", "ITEM_NM", "item_nm");
        Long amt = toLongByKeys(m, "amt", "AMT", "amount", "AMOUNT");

        // 최소한 itemId가 있어야 의미가 있으니, 없으면 null 처리(기존 normalize 로직과 동일한 방향)
        if (itemId == null || itemId.isBlank()) return null;

        PayItemRowVO vo = new PayItemRowVO();
        vo.setItemTy(itemTy);
        vo.setItemId(itemId);
        vo.setItemNm(itemNm);
        vo.setAmt(amt == null ? 0L : amt);
        return vo;
    }

    private String toStrByKeys(Map<?, ?> m, String... keys) {
        for (String k : keys) {
            if (k == null) continue;
            if (m.containsKey(k)) {
                Object v = m.get(k);
                if (v != null) return String.valueOf(v);
            }
        }
        return null;
    }

    private Long toLongByKeys(Map<?, ?> m, String... keys) {
        for (String k : keys) {
            if (k == null) continue;
            if (m.containsKey(k)) {
                Object v = m.get(k);
                if (v == null) return null;
                if (v instanceof Number n) return n.longValue();
                try { return Long.parseLong(String.valueOf(v)); } catch (Exception ignore) {}
            }
        }
        return null;
    }
}
