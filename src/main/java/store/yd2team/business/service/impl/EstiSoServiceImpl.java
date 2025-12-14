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

        // 세션 정보
        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        vo.setVendId(vendId);
        vo.setCreaBy(empId);
        vo.setUpdtBy(empId); // 이력 구조라 실사용은 안 하지만 유지

        // 상세 검증
        List<EstiSoDetailVO> detailList = vo.getDetailList();
        if (detailList == null || detailList.isEmpty()) {
            throw new IllegalArgumentException("견적 상세가 존재하지 않습니다.");
        }

        // 금액 합계 계산
        long totalSupplyAmt = 0L;
        for (EstiSoDetailVO d : detailList) {
            if (d.getSupplyAmt() != null) {
                totalSupplyAmt += d.getSupplyAmt();
            }
        }
        vo.setTtSupplyAmt(totalSupplyAmt);

        // 신규든 수정이든 새로 INSERT 되는 견적은 항상 승인대기(es1)
        vo.setEstiSt("es1");

        // 헤더 INSERT (이력 방식)
        if (vo.getEstiId() == null || vo.getEstiId().isEmpty()) {

            // 신규 견적서
            vo.setVersion("ver1");
            estiSoMapper.insertNewEsti(vo);

        } else {

            // 수정 (기존 estiId + 신규 버전 INSERT)
            String curVerStr = estiSoMapper.selectCurrentVersion(vo.getEstiId());

            int curVer = 0;
            if (curVerStr != null && curVerStr.startsWith("ver")) {
                curVer = Integer.parseInt(curVerStr.substring(3));
            }

            vo.setVersion("ver" + (curVer + 1));
            estiSoMapper.insertEstiHistory(vo);
        }

        // 상세 INSERT
        for (EstiSoDetailVO d : detailList) {

            d.setEstiId(vo.getEstiId());
            d.setVersion(vo.getVersion());

            // 세션 정보
            d.setVendId(vendId);
            d.setCreaBy(empId);
            d.setUpdtBy(empId);

            estiSoMapper.insertEstiDetail(d);
        }

        return vo.getEstiId();
    }
    
    // 이력보기 모달
    @Override
    public List<EstiSoVO> getEstiHistory(String estiId) {
        return estiSoMapper.selectEstiHistory(estiId);
    }
    
    
    // 이력보기의 보기 모달
    @Override
    public EstiSoVO getEstiByVersion(String estiId, String version) {
        EstiSoVO header = estiSoMapper.selectEstiHeaderByVersion(estiId, version);
        if (header != null) {
            header.setDetailList(estiSoMapper.selectEstiDetailListByVersion(estiId, version));
        }
        return header;
    }
    
	/*
	 * @Override
	 * 
	 * @Transactional public String saveEsti(EstiSoVO vo) {
	 * 
	 * // 세션 정보 String vendId = LoginSession.getVendId(); String empId =
	 * LoginSession.getEmpId();
	 * 
	 * vo.setVendId(vendId); vo.setCreaBy(empId); vo.setUpdtBy(empId); // 이력 구조라
	 * 실사용은 안 하지만 유지
	 * 
	 * 
	 * // 상태 기본값 if (vo.getEstiSt() == null || vo.getEstiSt().isEmpty()) {
	 * vo.setEstiSt("es1"); // 작성중 }
	 * 
	 * 
	 * // 상세 검증 + 합계 계산 List<EstiSoDetailVO> detailList = vo.getDetailList();
	 * 
	 * if (detailList == null || detailList.isEmpty()) { throw new
	 * IllegalArgumentException("견적 상세가 존재하지 않습니다."); }
	 * 
	 * long totalSupplyAmt = 0L; for (EstiSoDetailVO d : detailList) { if
	 * (d.getSupplyAmt() != null) { totalSupplyAmt += d.getSupplyAmt(); } }
	 * vo.setTtSupplyAmt(totalSupplyAmt);
	 * 
	 * 
	 * // 헤더 INSERT (이력 방식) if (vo.getEstiId() == null || vo.getEstiId().isEmpty())
	 * { // ▶ 신규 견적서 vo.setVersion("ver1"); estiSoMapper.insertNewEsti(vo);
	 * 
	 * } else { // ▶ 수정 (이력 INSERT) String curVerStr =
	 * estiSoMapper.selectCurrentVersion(vo.getEstiId());
	 * 
	 * int curVer = 0; if (curVerStr != null && curVerStr.startsWith("ver")) {
	 * curVer = Integer.parseInt(curVerStr.substring(3)); }
	 * 
	 * vo.setVersion("ver" + (curVer + 1)); estiSoMapper.insertEstiHistory(vo); }
	 * 
	 * 
	 * // 상세 INSERT for (EstiSoDetailVO d : detailList) {
	 * 
	 * d.setEstiId(vo.getEstiId()); d.setVersion(vo.getVersion());
	 * 
	 * // 세션 정보 d.setVendId(vendId); d.setCreaBy(empId); d.setUpdtBy(empId);
	 * 
	 * estiSoMapper.insertEstiDetail(d); }
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
    
}