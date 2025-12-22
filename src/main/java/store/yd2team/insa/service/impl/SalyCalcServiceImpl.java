package store.yd2team.insa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(transactionManager = "mybatisTxManager", readOnly = true)
    public Map<String, Object> previewSalyLedg(String salyLedgId,
                                               Long grpNo,
                                               List<String> salySpecIdList,
                                               String vendId,
                                               String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank())
            throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null)
            throw new IllegalArgumentException("grpNo is required");
        if (salySpecIdList == null || salySpecIdList.isEmpty())
            throw new IllegalArgumentException("salySpecIdList is empty");

        Map<String, Long> allowDispMap = buildAllowDispMap(vendId, grpNo);
        Map<String, Long> ducDispMap   = buildDucDispMap(vendId, grpNo);

        // ✅ 핵심: salySpecId → 결과 Map
        Map<String, Object> result = new HashMap<>();

        for (String salySpecId : salySpecIdList) {
            if (salySpecId == null || salySpecId.isBlank()) continue;

            String empId = salyCalcMapper.selectEmpIdBySpecId(salySpecId);
            if (empId == null || empId.isBlank()) {
                throw new IllegalStateException(
                    "empId not found for salySpecId=" + salySpecId
                );
            }

            CalcResult one = calcOnce(
                salyLedgId,
                empId,
                grpNo,
                allowDispMap,
                ducDispMap
            );
         // ✅ [추가] items에 dispNo 주입 (프론트가 calc_A_1 같은 컬럼에 꽂기 위해 필수)
            if (one.items != null) {
                for (PayItemRowVO it : one.items) {
                    if (it == null) continue;

                    String ty = it.getItemTy();    // "A" or "D"
                    String itemId = it.getItemId();

                    Long dispNo = null;
                    if ("A".equalsIgnoreCase(ty)) {
                        dispNo = allowDispMap.get(itemId);
                    } else if ("D".equalsIgnoreCase(ty)) {
                        dispNo = ducDispMap.get(itemId);
                    }

                    it.setDispNo(dispNo); // ✅ PayItemRowVO에 추가한 dispNo 세팅
                }
            }


            Map<String, Object> payload = new HashMap<>();
            payload.put("items", one.items);
            payload.put("payAmt", one.payAmt);
            payload.put("ttDucAmt", one.ttDucAmt);
            payload.put("actPayAmt", one.actPayAmt);

            // ✅ 이 줄이 핵심
            result.put(salySpecId, payload);
        }

        return result;
    }


    /**
     * ✅ 저장: 미리보기 결과를 DB에 반영
     * previewList: [{ salySpecId: "...", items: [ {itemTy,itemId,itemNm,amt}, ... ] }, ...]
     *
     * 핵심 변경:
     * - 저장/조회 키는 grpNo가 아니라 grpNm
     * - 삭제/정리는 (specId + grpNm) 기준
     * - “정렬/고유키”는 (itemTy + dispNo) 기준으로 잡아서, FK 끊어도 표시번호 기준으로 재조회 가능
     */
    @Override
    @Transactional(transactionManager = "mybatisTxManager", propagation = Propagation.REQUIRES_NEW)
    public void savePreviewResult(String salyLedgId, Long grpNo, List<Map<String, Object>> previewList,
                                  String saveMode,
                                  String vendId, String loginEmpId) {

        if (previewList == null) previewList = new ArrayList<Map<String, Object>>();

        log.info("[SAVE] start salyLedgId={}, grpNo={}, previewCnt={}", salyLedgId, grpNo, previewList.size());

        Map<String, Long> allowDispMap = buildAllowDispMap(vendId, grpNo);
        Map<String, Long> ducDispMap   = buildDucDispMap(vendId, grpNo);
        
     // ✅ dispNo -> itemNm 복원용 맵 (REPLACE 저장에서 itemNm이 비면 채우기)
        Map<Long, String> allowNmByDisp = new HashMap<>();
        List<AllowDucVO> allowListForNm = salyCalcMapper.selectAllowListForCalc(vendId, grpNo);
        if (allowListForNm != null) {
            for (AllowDucVO vo : allowListForNm) {
                if (vo != null && vo.getDispNo() != null) {
                    allowNmByDisp.put(vo.getDispNo().longValue(), vo.getAllowNm());
                }
            }
        }

        Map<Long, String> ducNmByDisp = new HashMap<>();
        List<AllowDucVO> ducListForNm = salyCalcMapper.selectDucListForCalc(vendId, grpNo);
        if (ducListForNm != null) {
            for (AllowDucVO vo : ducListForNm) {
                if (vo != null && vo.getDispNo() != null) {
                    ducNmByDisp.put(vo.getDispNo().longValue(), vo.getDucNm());
                }
            }
        }

        // ✅ grpNo -> grpNm (저장/조회 키)
        String grpNm = salyCalcMapper.selectGrpNm(vendId, grpNo);
        if (grpNm == null) grpNm = "";
        grpNm = grpNm.trim();

        for (Map<String, Object> one : previewList) {

            String salySpecId = (one == null) ? null : (String) one.get("salySpecId");
            if (salySpecId == null || salySpecId.isBlank()) continue;
            
            if (saveMode == null || saveMode.isBlank()) saveMode = "REPLACE";
            if ("REPLACE".equalsIgnoreCase(saveMode)) {
                salyCalcMapper.deleteSpecItemsBySpecId(salySpecId);
            }

            List<PayItemRowVO> items = coercePayItemRows(one.get("items"));
            CalcResult cr = normalizeItems(items == null ? new ArrayList<PayItemRowVO>() : items, allowDispMap, ducDispMap);

            // ✅ (B안) 항목별 delete/merge (NOT IN 삭제 사용 안 함)
            if (cr != null && cr.items != null) {
                for (PayItemRowVO it : cr.items) {
                    if (it == null) continue;

                    String itemTy = it.getItemTy();
                    if (itemTy == null || itemTy.isBlank()) continue;

                    // dispNo 우선 (manual edit save는 dispNo가 핵심)
                    Long dispNoLong = it.getDispNo();

                    if (dispNoLong == null) {
                        String itemId = it.getItemId();
                        if (itemId != null && !itemId.isBlank()) {
                            String dispNoStr = resolveDispNo(itemTy, itemId, allowDispMap, ducDispMap);
                            try {
                                dispNoLong = Long.parseLong(dispNoStr);
                            } catch (Exception e) {
                                continue; // dispNo 복원 실패면 스킵(잘못 저장/삭제 방지)
                            }
                        } else {
                            continue; // dispNo도 itemId도 없으면 식별 불가
                        }
                    }

                    long amt = (it.getAmt() == null ? 0L : it.getAmt().longValue());

                    if (amt == 0L) {
                        // ✅ 0이면 그 항목(한 칸)만 삭제
                        salyCalcMapper.deleteSpecItemByKey(salySpecId, grpNm, itemTy, dispNoLong);
                        continue;
                    }

                    // ✅ 0이 아니면 merge
                    Map<String, Object> m = new HashMap<String, Object>();
                    m.put("salySpecId", salySpecId);
                    m.put("grpNm", grpNm);
                    m.put("itemTy", itemTy);
                    m.put("dispNo", dispNoLong);

                 // ✅ itemNm은 항상 세팅 (PATCH여도 INSERT 대비 필수)
                    String nm = (it.getItemNm() == null ? "" : String.valueOf(it.getItemNm()).trim());

                    // itemNm이 비어있으면 dispNo로 복원
                    if (nm.isBlank()) {
                        if ("A".equalsIgnoreCase(itemTy)) {
                            nm = String.valueOf(allowNmByDisp.getOrDefault(dispNoLong, ""));
                        } else if ("D".equalsIgnoreCase(itemTy)) {
                            nm = String.valueOf(ducNmByDisp.getOrDefault(dispNoLong, ""));
                        }
                    }

                    // 최후 fallback
                    if (nm.isBlank()) nm = "(" + itemTy + dispNoLong + ")";

                    m.put("itemNm", nm);


                 m.put("amt", amt);

                    m.put("creaBy", loginEmpId);
                    m.put("updtBy", loginEmpId);

                    salyCalcMapper.mergeSpecItem(m);
                }
            }

            // ✅ (1번) 합계는 DB에서 재합산해서 update (반드시 for(one) 안에서 실행)
            Map<String, Object> totals = salyCalcMapper.selectSpecTotalsByGrpNm(salySpecId, grpNm);

            Object pObj = (totals == null ? null : totals.get("payAmt"));
            if (pObj == null && totals != null) pObj = totals.get("PAYAMT");

            Object dObj = (totals == null ? null : totals.get("ttDucAmt"));
            if (dObj == null && totals != null) dObj = totals.get("TTDUCAMT");

            long payAmt = (pObj instanceof Number) ? ((Number) pObj).longValue() : 0L;
            long ttDucAmt = (dObj instanceof Number) ? ((Number) dObj).longValue() : 0L;
            long actPayAmt = payAmt - ttDucAmt;

            Map<String, Object> upd = new HashMap<String, Object>();
            upd.put("salySpecId", salySpecId);
            upd.put("payAmt", payAmt);
            upd.put("ttDucAmt", ttDucAmt);
            upd.put("actPayAmt", actPayAmt);
            upd.put("updtBy", loginEmpId);

            salyCalcMapper.updateSalySpecTotals(upd);
        }

        log.info("[SAVE] end {}", salyLedgId);
    }


    // (기존) 급여계산 실행 (선택 사원만) - 필요 시 유지
    @Override
    @Transactional(transactionManager = "mybatisTxManager")
    public void calculateSalyLedg(String salyLedgId, Long grpNo, List<String> salySpecIdList,
                                  String vendId, String loginEmpId) {

        if (salyLedgId == null || salyLedgId.isBlank()) throw new IllegalArgumentException("salyLedgId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");
        if (salySpecIdList == null || salySpecIdList.isEmpty()) throw new IllegalArgumentException("salySpecIdList is empty");

        // calculateSalyLedg는 "바로 저장" 성격이었는데,
        // 지금 구조는 "미리보기 -> savePreviewResult"가 메인이라
        // 이 메서드는 미리보기/저장 흐름을 호출하는 용도로만 유지.
        // (호출하는 컨트롤러/JS가 있으면 기존 기능 안 깨짐)

        Map<String, Object> preview = previewSalyLedg(salyLedgId, grpNo, salySpecIdList, vendId, loginEmpId);

        List<Map<String, Object>> previewList = new ArrayList<>();
        for (String specId : salySpecIdList) {
            Object payloadObj = preview.get(specId);
            if (!(payloadObj instanceof Map)) continue;

            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) payloadObj;

            Map<String, Object> one = new HashMap<>();
            one.put("salySpecId", specId);
            one.put("items", payload.get("items"));
            previewList.add(one);
        }

        savePreviewResult(salyLedgId, grpNo, previewList, "REPLACE", vendId, loginEmpId);
    }

    /**
     * ✅ 저장된 급여명세서 항목 조회 (grpNm 기준)
     */
    @Override
    public List<SalySpecItemVO> getSalySpecItems(String salySpecId, Long grpNo, String vendId) {
        if (salySpecId == null || salySpecId.isBlank()) return List.of();
        if (vendId == null || vendId.isBlank()) return List.of();
        if (grpNo == null) return List.of();

        String grpNm = salyCalcMapper.selectGrpNm(vendId, grpNo);
        if (grpNm == null || grpNm.isBlank()) return List.of();

        return salyCalcMapper.selectSalySpecItemsByGrpNm(salySpecId, grpNm);
    }

    // =========================
    // 기존 그룹(계산그룹) 저장/삭제 로직 (가능한 그대로 유지)
    // =========================

    @Override
    @Transactional(transactionManager = "mybatisTxManager")
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
    @Transactional(transactionManager = "mybatisTxManager")
    public void saveCalcGroupAll(String vendId, String empId,
                                 List<Map<String, Object>> createdRows,
                                 List<Map<String, Object>> updatedRows,
                                 List<Map<String, Object>> deletedRows) {

        // createdRows: {grpNm, itemIds(List<String> or csv)} 형태로 오던 기존 로직을 지웅님 코드에 맞춰 그대로 쓰세요.
        // 여기선 “파일 통교체” 목적이라, 기존 구현을 최대한 보수적으로 유지합니다.
        // (지웅님이 다음 대화에 해당 메서드 원본/프론트 payload를 주면 정확히 맞춰서 고정해드릴게요.)

        if (createdRows != null) {
            for (Map<String, Object> row : createdRows) {
                String grpNm = asString(row.get("grpNm"));
                @SuppressWarnings("unchecked")
                List<String> itemIds = (List<String>) row.get("itemIds");
                saveCalcGroup(vendId, empId, null, grpNm, itemIds);
            }
        }

        if (updatedRows != null) {
            for (Map<String, Object> row : updatedRows) {
                Long grpNo = asLong(row.get("grpNo"));
                String grpNm = asString(row.get("grpNm"));
                @SuppressWarnings("unchecked")
                List<String> itemIds = (List<String>) row.get("itemIds");
                saveCalcGroup(vendId, empId, grpNo, grpNm, itemIds);
            }
        }

        if (deletedRows != null) {
            for (Map<String, Object> row : deletedRows) {
                Long grpNo = asLong(row.get("grpNo"));
                if (grpNo != null) deleteCalcGroup(vendId, grpNo);
            }
        }
    }

    @Override
    @Transactional(transactionManager = "mybatisTxManager")
    public void deleteCalcGroup(String vendId, Long grpNo) {
        if (vendId == null || vendId.isBlank()) throw new IllegalArgumentException("vendId is required");
        if (grpNo == null) throw new IllegalArgumentException("grpNo is required");

        salyCalcMapper.deleteGrpItemsByGrpNo(vendId, grpNo);
        salyCalcMapper.deleteCalcGroup(vendId, grpNo);
    }

    // 아래 3개는 "급여계산 초기화" 등에서 쓰는 기존 메서드들.
    // 지웅님이 다음 파일(Mapper.xml/Controller/HTML)까지 맞춰서 보내주면, DB 맞춤으로 정확히 이어서 수정해줄게요.
    @Override
    public int deleteSpecItemsByLedgId(String salyLedgId, String vendId) {
        // 기존 구현/SQL이 다음 대화에 나오면 그거로 통일
        throw new UnsupportedOperationException("deleteSpecItemsByLedgId: 구현 파일/SQL을 보내주면 맞춰서 연결해드릴게요.");
    }

    @Override
    public int resetSalySpecTotalsByLedgId(String salyLedgId, String vendId, String updtBy) {
        throw new UnsupportedOperationException("resetSalySpecTotalsByLedgId: 구현 파일/SQL을 보내주면 맞춰서 연결해드릴게요.");
    }


    @Override
    public Map<String, Object> getSavedCalcItems(String salyLedgId, Long grpNo, String vendId) {
        // 이 메서드는 보통 “재진입 시 저장된 항목을 모달에 뿌리는 용도”인데,
        // 지웅님 HTML/JS 쪽 구조를 보고 리턴 형태를 확정해야 함.
        throw new UnsupportedOperationException("getSavedCalcItems: HTML/JS에서 기대하는 응답 형태를 보내주면 맞춰서 구현해드릴게요.");
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

        List<PayItemRowVO> items = coercePayItemRows(p.get("o_result"));
        return normalizeItems(items == null ? List.of() : items, allowDispMap, ducDispMap);
    }

    /**
     * 1) 표시번호 기준 정렬
     * 2) (itemTy + itemId) 중복 제거
     * 3) 합계 계산
     */
    private CalcResult normalizeItems(List<PayItemRowVO> raw,
                                      Map<String, Long> allowDispMap,
                                      Map<String, Long> ducDispMap) {

        if (raw == null) raw = List.of();

        // dispNo 기준 정렬
        List<PayItemRowVO> sorted = raw.stream()
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    long ad = resolveDispNoLong(a.getItemTy(), a.getItemId(), allowDispMap, ducDispMap);
                    long bd = resolveDispNoLong(b.getItemTy(), b.getItemId(), allowDispMap, ducDispMap);
                    if (ad != bd) return Long.compare(ad, bd);

                    // 같은 dispNo면 A가 먼저(수당 먼저), 그 다음 이름
                    int aty = ("A".equalsIgnoreCase(a.getItemTy()) ? 0 : 1);
                    int bty = ("A".equalsIgnoreCase(b.getItemTy()) ? 0 : 1);
                    if (aty != bty) return Integer.compare(aty, bty);

                    String an = a.getItemNm() == null ? "" : a.getItemNm();
                    String bn = b.getItemNm() == null ? "" : b.getItemNm();
                    return an.compareTo(bn);
                })
                .toList();

        // (itemTy + itemId) 중복 제거: 마지막 값 유지
        Map<String, PayItemRowVO> dedup = new LinkedHashMap<>();
        for (PayItemRowVO it : sorted) {
            String ty = (it.getItemTy() == null ? "" : it.getItemTy().toUpperCase(Locale.ROOT));
            String id = (it.getItemId() == null ? "" : it.getItemId());
            String key = ty + "_" + id;
            if (key.isBlank()) continue;
            dedup.put(key, it);
        }
        List<PayItemRowVO> finalItems = new ArrayList<>(dedup.values());

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

    @SuppressWarnings("unchecked")
    private List<PayItemRowVO> coercePayItemRows(Object obj) {
        if (obj == null) return List.of();

        if (obj instanceof List<?> list) {
            if (list.isEmpty()) return List.of();

            Object first = list.get(0);
            if (first instanceof PayItemRowVO) {
                return (List<PayItemRowVO>) list;
            }

            // List<Map> -> PayItemRowVO 변환
            if (first instanceof Map) {
                List<PayItemRowVO> out = new ArrayList<>();
                for (Object o : list) {
                    if (!(o instanceof Map)) continue;
                    Map<String, Object> m = (Map<String, Object>) o;

                    String itemTy = asString(m.get("itemTy"));
                    String itemId = asString(m.get("itemId"));
                    String itemNm = asString(m.get("itemNm"));
                    Long amt = asLong(m.get("amt"));
                    Long dispNo = asLong(m.get("dispNo"));

                    out.add(PayItemRowVO.builder()
                            .itemTy(itemTy)
                            .itemId(itemId)
                            .itemNm(itemNm)
                            .dispNo(dispNo)
                            .amt(amt)
                            .build());
                }
                return out;
            }
        }

        return List.of();
    }

    private String resolveDispNo(String itemTy, String itemId,
            Map<String, Long> allowDispMap,
            Map<String, Long> ducDispMap) {
	long v = resolveDispNoLong(itemTy, itemId, allowDispMap, ducDispMap);
	return Long.toString(v);
	}
	
    private long resolveDispNoLong(String itemTy, String itemId,
            Map<String, Long> allowDispMap,
            Map<String, Long> ducDispMap) {

		// ✅ 0) itemId가 숫자면(예: "9") = manual edit save에서 dispNo를 itemId로 보낸 케이스
		//    이 경우 그대로 dispNo로 인정
		if (itemId != null && itemId.matches("\\d+")) {
		try { return Long.parseLong(itemId); }
		catch (Exception ignore) {}
		}
		
		// ✅ 1) itemId 없으면 “매핑 실패”로 보고 0 반환 (999999 같은 큰수 금지)
		if (itemId == null || itemId.isBlank()) return 0L;
		
		if ("A".equalsIgnoreCase(itemTy)) return allowDispMap.getOrDefault(itemId, 0L);
		if ("D".equalsIgnoreCase(itemTy)) return ducDispMap.getOrDefault(itemId, 0L);
		return 0L;
		}



    private String makeKey(String itemTy, String dispNo) {
        String ty = (itemTy == null ? "" : itemTy.toUpperCase(Locale.ROOT));
        String dn = (dispNo == null ? "" : dispNo);
        return ty + "_" + dn;
    }

    private List<Map<String, Object>> buildGrpItemRows(String vendId, Long grpNo, String empId, List<String> itemIds) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (itemIds == null) return list;

        for (String itemId : itemIds) {
            if (itemId == null || itemId.isBlank()) continue;

            // ✅ 여기 추가
            String up = itemId.trim().toUpperCase(Locale.ROOT);
            String itemTy;
            if (up.startsWith("ALW")) itemTy = "A";      // 수당
            else if (up.startsWith("DUC")) itemTy = "D"; // 공제
            else {
                throw new IllegalArgumentException("itemTy 판단 불가 itemId=" + itemId);
            }

            Map<String, Object> row = new HashMap<>();
            row.put("vendId", vendId);
            row.put("grpNo", grpNo);
            row.put("itemTy", itemTy);   // ✅ 이 줄이 핵심
            row.put("itemId", itemId);
            row.put("creaBy", empId);
            list.add(row);
        }
        return list;
    }


    private String asString(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }

    private Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); }
        catch (Exception e) { return null; }
    }
    
    @Override
    public List<SalySpecItemVO> getSalySpecItemsByGrpNm(String salySpecId,
                                                        String grpNm,
                                                        String vendId) {
        return salyCalcMapper.selectSalySpecItemsByGrpNm(salySpecId, grpNm);
    }
    /**
     * ✅ 재조회용: saly_spec_id 기준 전체 조회
     * - grpNm 조건 없이 전부 가져온다
     * - 화면에서 calcGrpNm 기준으로 필터링
     */
    @Override
    public List<SalySpecItemVO> getSalySpecItemsBySpecId(String salySpecId,
                                                         String vendId) {
        if (salySpecId == null || salySpecId.isBlank()) return List.of();
        // vendId는 현재 SQL에 안 쓰지만 시그니처 통일용
        return salyCalcMapper.selectSalySpecItemsBySpecId(salySpecId);
    }
    @Override
    @Transactional
    public void resetSalyCalc(String salyLedgId, String vendId, String loginEmpId) {
        if (salyLedgId == null || salyLedgId.isBlank()) {
            throw new IllegalArgumentException("급여대장ID가 없습니다.");
        }

        // ✅ 핵심: 아이템 싹 삭제 (트리거가 합계 0 처리/tt_pay_amt 반영)
        salyCalcMapper.deleteSpecItemsByLedgId(salyLedgId, vendId);

        // (선택) 필요하면 여기서 tb_saly_spec 합계 0 업데이트도 넣을 수 있지만,
        // 지웅님이 "트리거로 된다"라고 했으니 생략.
    }


}
