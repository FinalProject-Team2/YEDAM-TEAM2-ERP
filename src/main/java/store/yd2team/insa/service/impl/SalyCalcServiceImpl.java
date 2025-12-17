package store.yd2team.insa.service.impl;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.insa.mapper.SalyCalcMapper;
import store.yd2team.insa.service.AllowDucVO;
import store.yd2team.insa.service.CalGrpVO;
import store.yd2team.insa.service.PayItemRowVO;
import store.yd2team.insa.service.SalyCalcService;
import store.yd2team.insa.service.SalySpecCalcViewVO;
import store.yd2team.insa.service.SalySpecItemVO;

@Slf4j
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
    @Transactional(
    	    transactionManager = "mybatisTxManager",
    	    readOnly = true
    	)
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
		    @Transactional(
		        transactionManager = "mybatisTxManager",
		        propagation = Propagation.REQUIRES_NEW
		    )

	    public void savePreviewResult(String salyLedgId, Long grpNo, List<Map<String, Object>> previewList,
	                                  String vendId, String loginEmpId) {

	        log.info("[SAVE] start salyLedgId={}, grpNo={}, previewCnt={}", salyLedgId, grpNo, previewList.size());

	        Map<String, Long> allowDispMap = buildAllowDispMap(vendId, grpNo);
	        Map<String, Long> ducDispMap   = buildDucDispMap(vendId, grpNo);

	        for (Map<String, Object> one : previewList) {
	            String salySpecId = (String) one.get("salySpecId");
	            log.info("[SAVE] spec start {}", salySpecId);

	            List<PayItemRowVO> items = coercePayItemRows(one.get("items"));
	            CalcResult cr = normalizeItems(items == null ? List.of() : items, allowDispMap, ducDispMap);

	            // ✅ itemId 기준 중복 제거(마지막 값 유지) + 0원은 제외(저장/집계 불필요)
	            Map<String, PayItemRowVO> uniqMap = new LinkedHashMap<>();
	            for (PayItemRowVO it : cr.items) {
	                if (it == null) continue;
	                if (it.getItemId() == null || it.getItemId().isBlank()) continue;

	                long amt = (it.getAmt() == null ? 0L : it.getAmt());
	                if (amt == 0L) continue;

	                uniqMap.put(it.getItemId(), it); // 뒤에 온 값으로 덮어씀
	            }
	            List<PayItemRowVO> uniqItems = new ArrayList<>(uniqMap.values());

	            // ✅ (핵심) "그룹 덮어씌우기" 저장 규칙:
	            // 1) 이번 저장에 포함된 itemId 외에는 (spec+grp)에서 삭제  -> 항목 빠진건 삭제
	            // 2) 포함된 itemId는 MERGE(있으면 UPDATE, 없으면 INSERT) -> 같은 항목은 값 UPDATE, 새 항목은 INSERT
	            // 3) 0원은 애초에 저장 대상에서 제외 (위에서 필터링)
	            List<String> itemIdList = uniqItems.stream()
	                    .map(PayItemRowVO::getItemId)
	                    .filter(Objects::nonNull).filter(s -> !s.isBlank())
	                    .distinct()
	                    .toList();

	            if (itemIdList.isEmpty()) {
	                // 전부 0원이거나 항목이 없으면: 해당 spec+grp 항목 전체 삭제
	                salyCalcMapper.deleteSpecItemsBySpecAndGrp(salySpecId, grpNo);
	            } else {
	                // 이번 itemIdList에 없는 기존 항목은 삭제
	                Map<String, Object> delNotIn = new HashMap<>();
	                delNotIn.put("salySpecId", salySpecId);
	                delNotIn.put("grpNo", grpNo);
	                delNotIn.put("itemIdList", itemIdList);
	                salyCalcMapper.deleteSpecItemsBySpecAndItemIdsNotIn(delNotIn);

	                // 포함된 항목은 MERGE
	                for (PayItemRowVO it : uniqItems) {
	                    Map<String, Object> m = new HashMap<>();
	                    m.put("salySpecId", salySpecId);
	                    m.put("grpNo", grpNo);
	                    m.put("itemTy", it.getItemTy());
	                    m.put("itemId", it.getItemId());
	                    m.put("itemNm", it.getItemNm());
	                    m.put("amt", it.getAmt() == null ? 0L : it.getAmt());
	                    m.put("loginEmpId", loginEmpId);
	                    // MERGE의 NOT MATCHED INSERT용 PK 채우기
	                    m.put("creaBy", loginEmpId);
	                    m.put("updtBy", loginEmpId);
	                    salyCalcMapper.mergeSpecItem(m);
	                }
	            }

	            // 합계는 화면에 보이는(0 제외) 기준으로 갱신
	            long payAmt = 0L;
	            long ttDucAmt = 0L;
	            for (PayItemRowVO it : uniqItems) {
	                String itemTy = it.getItemTy();
	                long amt = (it.getAmt() == null ? 0L : it.getAmt());
	                if ("A".equalsIgnoreCase(itemTy)) payAmt += amt;
	                else if ("D".equalsIgnoreCase(itemTy)) ttDucAmt += amt;
	            }
	            long actPayAmt = payAmt - ttDucAmt;

	            log.info("[SAVE] updateSalySpecTotals {}", salySpecId);
	            Map<String, Object> upd = new HashMap<>();
	            upd.put("salySpecId", salySpecId);
	            upd.put("payAmt", payAmt);
	            upd.put("ttDucAmt", ttDucAmt);
	            upd.put("actPayAmt", actPayAmt);
	            upd.put("updtBy", loginEmpId);
	            salyCalcMapper.updateSalySpecTotals(upd);

	            log.info("[SAVE] spec end {}", salySpecId);
	        }

	        log.info("[SAVE] end {}", salyLedgId);
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
