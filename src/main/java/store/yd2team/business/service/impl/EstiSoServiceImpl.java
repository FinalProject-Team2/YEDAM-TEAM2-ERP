package store.yd2team.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.EstiSoMapper;
import store.yd2team.business.service.EstiSoDetailVO;
import store.yd2team.business.service.EstiSoService;
import store.yd2team.business.service.EstiSoVO;
import store.yd2team.business.service.OustVO;
import store.yd2team.common.util.LoginSession;

@Service
@RequiredArgsConstructor
public class EstiSoServiceImpl implements EstiSoService {

	private final EstiSoMapper estiSoMapper;

	// 견적서 조회
    @Override
    public List<EstiSoVO> selectEstiList(EstiSoVO cond) {
        return estiSoMapper.selectEsti(cond);
    }
    
    // 견적서 그리드 상태 업데이트
    @Override
    public int updateStatus(EstiSoVO vo) {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        vo.setVendId(vendId);
        vo.setUpdtBy(empId);

        return estiSoMapper.updateStatus(vo);
    }
    
    // 견적서 모달 상품명 auto complete
    @Override
    public List<EstiSoVO> searchProduct(String keyword) {
        return estiSoMapper.searchProduct(keyword);
    }

    @Override
    public EstiSoVO getProductDetail(String productId) {
        return estiSoMapper.getProductDetail(productId);
    }
    
    // 견적서 모달 고객사 auto complete
    @Override
    public List<EstiSoVO> searchCustcomId(String keyword) {
    	return estiSoMapper.searchCustcomId(keyword);
    }
    
    @Override
    public List<EstiSoVO> searchCustcomName(String keyword) {
        return estiSoMapper.searchCustcomName(keyword);
    }
    
   
    
    // 견적서 저장
    @Override
    @Transactional
    public String saveEsti(EstiSoVO vo) {

        // 🔥 세션 정보
        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        vo.setVendId(vendId);
        vo.setCreaBy(empId);
        vo.setUpdtBy(empId);

        // 0) 상태 기본값
        if (vo.getEstiSt() == null || vo.getEstiSt().isEmpty()) {
            vo.setEstiSt("es1");
        }

        // 1) 상세 합계
        long total = 0L;
        List<EstiSoDetailVO> list = vo.getDetailList();
        if (list != null) {
            for (EstiSoDetailVO d : list) {
                if (d.getSupplyAmt() != null) {
                    total += d.getSupplyAmt();
                }
            }
        }
        vo.setTtSupplyAmt(total);

        // 2) 헤더 INSERT (이력 방식)
        if (vo.getEstiId() == null || vo.getEstiId().isEmpty()) {
            vo.setVersion("ver1");
            estiSoMapper.insertEsti(vo);
        } else {
            String curVerStr = estiSoMapper.selectCurrentVersion(vo.getEstiId());
            int curVer = curVerStr == null ? 0 : Integer.parseInt(curVerStr.replace("ver", ""));
            vo.setVersion("ver" + (curVer + 1));
            estiSoMapper.insertEsti(vo);
        }

        // 3) 상세 INSERT
        if (list != null) {
            for (EstiSoDetailVO d : list) {

                d.setEstiId(vo.getEstiId());
                d.setVersion(vo.getVersion());

                // 🔥 상세에도 세션 정보
                d.setVendId(vendId);
                d.setCreaBy(empId);
                d.setUpdtBy(empId);

                estiSoMapper.insertEstiDetail(d);
            }
        }

        return vo.getEstiId();
    }
	/*
	 * @Override
	 * 
	 * @Transactional public String saveEsti(EstiSoVO vo) { // 0) 상태 기본값 (없으면 es1)
	 * if (vo.getEstiSt() == null || vo.getEstiSt().isEmpty()) {
	 * vo.setEstiSt("es1"); }
	 * 
	 * // 1) 상세 합계 Long total = 0L; List<EstiSoDetailVO> list = vo.getDetailList();
	 * if (list != null) { for (EstiSoDetailVO d : list) { if (d.getSupplyAmt() !=
	 * null) { total = d.getSupplyAmt(); } } } vo.setTtSupplyAmt(total);
	 * 
	 * // 2) version + estiId 처리 if (vo.getEstiId() == null ||
	 * vo.getEstiId().isEmpty()) { // 신규 vo.setVersion("ver1"); // ★ version 1
	 * estiSoMapper.insertEsti(vo); // ★ 여기서 selectKey 로 estiId 세팅 } else { // 수정
	 * (이력 INSERT) String curVerStr =
	 * estiSoMapper.selectCurrentVersion(vo.getEstiId()); int curVer = 0; if
	 * (curVerStr != null && !curVerStr.isEmpty()) { if
	 * (curVerStr.startsWith("ver")) { curVer =
	 * Integer.parseInt(curVerStr.replace("ver", "")); } else { curVer =
	 * Integer.parseInt(curVerStr); } } vo.setVersion("ver" + (curVer + 1)); // ★ 다음
	 * 버전 estiSoMapper.insertEsti(vo); }
	 * 
	 * // ★ 디버깅용 로그 System.out.println("HEADER AFTER INSERT >>> estiId=" +
	 * vo.getEstiId() + ", version=" + vo.getVersion());
	 * 
	 * // 3) 상세 INSERT if (list != null) { for (EstiSoDetailVO d : list) {
	 * 
	 * // 헤더 정보 복사 d.setEstiId(vo.getEstiId()); // ★ 여기서 estiId 세팅
	 * d.setVersion(vo.getVersion()); // ★ 여기서 version 세팅
	 * 
	 * estiSoMapper.insertEstiDetail(d);
	 * 
	 * System.out.println("DETAIL AFTER INSERT >>> detailNo=" + d.getEstiDetailNo()
	 * + ", estiId=" + d.getEstiId() + ", version=" + d.getVersion()); } }
	 * 
	 * return vo.getEstiId(); }
	 */
    
    @Override
    public EstiSoVO getEsti(String estiId) {
        EstiSoVO header = estiSoMapper.selectEstiHeader(estiId);
        if (header != null) {
            header.setDetailList(estiSoMapper.selectEstiDetailList(estiId));
        }
        return header;
    }
    
    
    
    // 주문서 등록버튼
    @Override
    public EstiSoVO getOrderInitFromEsti(String estiId) {
        EstiSoVO header = estiSoMapper.selectEstiHeader(estiId);
        List<EstiSoDetailVO> detailList = estiSoMapper.selectEstiDetailList(estiId);

        header.setDetailList(detailList);
        return header;
    }

    // 주문서 저장
    @Override
    public String saveOrderFromEsti(EstiSoVO vo) {

        // 🔥 세션 정보
        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        vo.setVendId(vendId);
        vo.setCreaBy(empId);
        vo.setUpdtBy(empId);

        // 1) 주문번호 생성
        String soId = estiSoMapper.createSoId();
        vo.setSoId(soId);

        // 2) 합계 계산
        long ttSupply = 0L;
        long ttQty = 0L;
        long ttVat = 0L;

        if (vo.getDetailList() != null) {
            for (EstiSoDetailVO d : vo.getDetailList()) {

                long supply = d.getSupplyAmt() == null ? 0L : d.getSupplyAmt();
                long qy     = d.getQy() == null ? 0L : d.getQy();

                ttSupply += supply;
                ttQty    += qy;
                ttVat    += supply / 10;

                // 🔥 주문 상세에도 세션 정보
                d.setVendId(vendId);
                d.setCreaBy(empId);
                d.setUpdtBy(empId);
            }
        }

        vo.setTtSupplyPrice(ttSupply);
        vo.setTtSurtaxPrice(ttVat);
        vo.setTtPrice(ttSupply + ttVat);
        vo.setTtSoQy(ttQty);

        // 3) 주문 헤더 INSERT
        estiSoMapper.insertSo(vo);

        // 4) 주문 상세 INSERT
        if (vo.getDetailList() != null) {
            for (EstiSoDetailVO d : vo.getDetailList()) {
                d.setSoId(soId);
                estiSoMapper.insertSoDetail(d);
            }
        }

        // 5) 견적 상태 변경 (주문 완료)
        estiSoMapper.updateEstiStatusToOrdered(
            vo.getEstiId(),
            vo.getVersion(),
            empId        // 🔥 누가 변경했는지
        );

        return soId;
    }
	/*
	 * @Override public String saveOrderFromEsti(EstiSoVO vo) {
	 * 
	 * // 1) 주문번호 생성 String soId = estiSoMapper.createSoId(); vo.setSoId(soId);
	 * 
	 * // 2) 합계 계산 Long ttSupply = 0L; Long ttQty = 0L; long ttVat = 0L;
	 * 
	 * if (vo.getDetailList() != null) { for (EstiSoDetailVO d : vo.getDetailList())
	 * { long supply = d.getSupplyAmt() == null ? 0L : d.getSupplyAmt(); long qy =
	 * d.getQy() == null ? 0L : d.getQy();
	 * 
	 * ttSupply += supply; ttQty += qy;
	 * 
	 * // 부가세 10% (소수점은 버림 기준 예시) ttVat += supply / 10; // 10% = supply * 0.1 }
	 * 
	 * vo.setTtSupplyPrice(ttSupply); vo.setTtSurtaxPrice(ttVat); //
	 * tb_so.TT_SURTAX_PRICE vo.setTtPrice(ttSupply + ttVat); // tb_so.TT_PRICE
	 * vo.setTtSoQy(ttQty); }
	 * 
	 * vo.setTtSupplyPrice(ttSupply); vo.setTtSoQy(ttQty);
	 * 
	 * // 3) 주문 헤더 INSERT estiSoMapper.insertSo(vo);
	 * 
	 * // 4) 주문 상세 INSERT if (vo.getDetailList() != null) { for (EstiSoDetailVO d :
	 * vo.getDetailList()) { d.setSoId(soId); estiSoMapper.insertSoDetail(d); } }
	 * 
	 * // 5) 견적 상태 es4(예: 주문완료)로 변경
	 * estiSoMapper.updateEstiStatusToOrdered(vo.getEstiId(), vo.getVersion());
	 * 
	 * 
	 * return soId; }
	 */
    
    
    
    // ================================================== 주문서관리
    // 주문서 조회
    @Override
    public List<EstiSoVO> selectSoList(EstiSoVO vo) {

        // 1) 헤더 목록 조회
        List<EstiSoVO> headerList = estiSoMapper.selectSoHeaderList(vo);

        // 2) 상세 조회 후 각 header에 매핑
        for (EstiSoVO header : headerList) {
            List<EstiSoDetailVO> details = estiSoMapper.selectSoDetailList(header.getSoId());
            header.setDetailList(details);

            // 대표상품명 + 외 n건 텍스트 만들기
            if (!details.isEmpty()) {
                header.setProductName(details.get(0).getProductName());
                if (details.size() > 1) {
                    header.setProductName(details.get(0).getProductName() + " 외 " + (details.size() - 1) + "건");
                }

				/*
				 * // 총수량 / 재고수량은 첫 번째 상품 기준 header.setTtSoQy(details.get(0).getQy());
				 * header.setCurrStockQy(details.get(0).getCurrStockQy());
				 */
            }
        }

        return headerList;
    }
    
    // 주문서관리화면 승인버튼
    @Override
    public void approveSo(List<EstiSoVO> list) throws Exception {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        for (EstiSoVO vo : list) {

            String currStatus = estiSoMapper.selectSoStatus(vo.getSoId());

            if (!("es1".equals(currStatus) || "es5".equals(currStatus))) {
                throw new RuntimeException(
                    "주문서 " + vo.getSoId() + "는 승인할 수 없는 상태입니다."
                );
            }

            estiSoMapper.updateSoStatusToApproved(
                vo.getSoId(),
                vendId,
                empId
            );
        }
    }
	/*
	 * @Override public void approveSo(List<EstiSoVO> list) throws Exception {
	 * 
	 * for (EstiSoVO vo : list) {
	 * 
	 * // 1. 현재 상태 조회 String currStatus = estiSoMapper.selectSoStatus(vo.getSoId());
	 * 
	 * // 승인 가능한 상태인지 확인 (es1: 대기, es5: 보류) if (!("es1".equals(currStatus) ||
	 * "es5".equals(currStatus))) { throw new RuntimeException("주문서 " + vo.getSoId()
	 * + "는 승인할 수 없는 상태입니다."); }
	 * 
	 * // 2. 승인 처리 (상태 = es2) estiSoMapper.updateSoStatusToApproved(vo.getSoId()); }
	 * }
	 */ // 주문서 승인버튼 end
    
    // 보류버튼 이벤트
    @Transactional
    @Override
    public Map<String, Object> rejectOrder(String soId, String reason) {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        EstiSoVO header = estiSoMapper.getOrderHeader(soId);

        if (header == null) {
            return Map.of("success", false, "message", "존재하지 않는 주문서입니다.");
        }

        String status = header.getProgrsSt();

        if ("es5".equals(status)) {
            return Map.of("success", false, "message", "이미 보류 상태입니다.");
        }

        if (!("es1".equals(status) || "es2".equals(status))) {
            return Map.of("success", false, "message", "이 상태에서는 보류할 수 없습니다.");
        }

        estiSoMapper.updateRejectStatus(
            soId,
            reason,
            vendId,
            empId
        );

        return Map.of(
            "success", true,
            "message", "보류 처리 완료되었습니다."
        );
    }
	/*
	 * @Transactional
	 * 
	 * @Override public Map<String, Object> rejectOrder(String soId, String reason)
	 * {
	 * 
	 * Map<String, Object> result = new HashMap<>();
	 * 
	 * EstiSoVO header = estiSoMapper.getOrderHeader(soId);
	 * 
	 * if (header == null) { result.put("success", false); result.put("message",
	 * "존재하지 않는 주문서입니다."); return result; }
	 * 
	 * String status = header.getProgrsSt();
	 * 
	 * // 이미 보류 상태 if ("es5".equals(status)) { result.put("success", false);
	 * result.put("message", "이미 보류 상태입니다."); return result; }
	 * 
	 * // 승인대기(es1), 승인(es2), 반려(es3) 등 → 보류 가능 // 필요 시 조건 조정 가능 if
	 * (!("es1".equals(status) || "es2".equals(status))) { result.put("success",
	 * false); result.put("message", "이 상태에서는 보류할 수 없습니다."); return result; }
	 * 
	 * estiSoMapper.updateRejectStatus(soId, reason);
	 * 
	 * result.put("success", true); result.put("message", "보류 처리 완료되었습니다."); return
	 * result; }
	 */
    
    // 주문취소버튼 이벤트
    @Transactional
    @Override
    public Map<String, Object> cancelOrder(String soId, String reason) {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        EstiSoVO header = estiSoMapper.getOrderHeader(soId);

        if (header == null) {
            return Map.of("success", false, "message", "존재하지 않는 주문서입니다.");
        }

        String status = header.getProgrsSt();

        if ("es9".equals(status)) {
            return Map.of("success", false, "message", "이미 취소된 주문서입니다.");
        }

        if ("es2".equals(status)) {
            return Map.of("success", false, "message", "승인 상태에서는 취소할 수 없습니다.");
        }

        if (!("es1".equals(status) || "es5".equals(status))) {
            return Map.of("success", false, "message", "취소할 수 없는 상태입니다.");
        }

        estiSoMapper.updateCancelStatus(
            soId,
            reason,
            vendId,
            empId
        );

        return Map.of(
            "success", true,
            "message", "취소 처리 완료되었습니다."
        );
    }
	/*
	 * @Transactional
	 * 
	 * @Override public Map<String, Object> cancelOrder(String soId, String reason)
	 * {
	 * 
	 * Map<String, Object> result = new HashMap<>();
	 * 
	 * EstiSoVO header = estiSoMapper.getOrderHeader(soId);
	 * 
	 * if (header == null) { result.put("success", false); result.put("message",
	 * "존재하지 않는 주문서입니다."); return result; }
	 * 
	 * String status = header.getProgrsSt();
	 * 
	 * // 이미 취소 상태 if ("es9".equals(status)) { result.put("success", false);
	 * result.put("message", "이미 취소된 주문서입니다."); return result; }
	 * 
	 * // 승인 상태는 취소 불가 if ("es2".equals(status)) { result.put("success", false);
	 * result.put("message", "승인 상태에서는 취소할 수 없습니다."); return result; }
	 * 
	 * // 승인대기(es1), 보류(es5)만 취소 가능 if (!("es1".equals(status) ||
	 * "es5".equals(status))) { result.put("success", false); result.put("message",
	 * "취소할 수 없는 상태입니다."); return result; }
	 * 
	 * estiSoMapper.updateCancelStatus(soId, reason);
	 * 
	 * result.put("success", true); result.put("message", "취소 처리 완료되었습니다."); return
	 * result; }
	 */
    
    
    // 출하지시서 작성 저장 버튼
    @Override
    public void saveOust(OustVO vo) throws Exception {

        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        vo.setVendId(vendId);
        vo.setCreaBy(empId);
        vo.setUpdtBy(empId);

        // 1) 출하지시서 INSERT
        estiSoMapper.insertOust(vo);

        // 2) 주문 상태 변경 (출하지시)
        estiSoMapper.updateSoStatus(
            vo.getSoId(),
            "es6",
            vendId,
            empId
        );
    }
	/*
	 * @Override public void saveOust(OustVO vo) throws Exception {
	 * 
	 * // 1) 출하지시서 INSERT estiSoMapper.insertOust(vo);
	 * 
	 * // 2) 주문서 상태 es6 로 변경 estiSoMapper.updateSoStatus(vo.getSoId(), "es6"); }
	 */
    
}