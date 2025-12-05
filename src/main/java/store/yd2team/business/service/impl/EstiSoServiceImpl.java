package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.EstiSoMapper;
import store.yd2team.business.service.EstiSoDetailVO;
import store.yd2team.business.service.EstiSoService;
import store.yd2team.business.service.EstiSoVO;

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
    
   
    
    
    
    @Override
    @Transactional
    public String saveEsti(EstiSoVO vo) {
    	// 0) 상태 기본값 (없으면 es1)
        if (vo.getEstiSt() == null || vo.getEstiSt().isEmpty()) {
            vo.setEstiSt("es1");
        }

        // 1) 상세 합계
        Long total = 0L;
        List<EstiSoDetailVO> list = vo.getDetailList();
        if (list != null) {
            for (EstiSoDetailVO d : list) {
                if (d.getSupplyAmt() != null) {
                	total = d.getSupplyAmt();
                }
            }
        }
        vo.setTtSupplyAmt(total);

        // 2) version + estiId 처리
        if (vo.getEstiId() == null || vo.getEstiId().isEmpty()) {
            // 신규
            vo.setVersion("ver1");              // ★ version 1
            estiSoMapper.insertEsti(vo);     // ★ 여기서 selectKey 로 estiId 세팅
        } else {
            // 수정 (이력 INSERT)
            String curVerStr = estiSoMapper.selectCurrentVersion(vo.getEstiId());
            int curVer = 0;
            if (curVerStr != null && !curVerStr.isEmpty()) {
            	if (curVerStr.startsWith("ver")) {
                    curVer = Integer.parseInt(curVerStr.replace("ver", ""));
                } else {
                    curVer = Integer.parseInt(curVerStr);
                }
            }
            vo.setVersion("ver" + (curVer + 1));  // ★ 다음 버전
            estiSoMapper.insertEsti(vo);
        }

        // ★ 디버깅용 로그
        System.out.println("HEADER AFTER INSERT >>> estiId=" + vo.getEstiId()
                + ", version=" + vo.getVersion());

        // 3) 상세 INSERT
        if (list != null) {
            for (EstiSoDetailVO d : list) {

                // 헤더 정보 복사
                d.setEstiId(vo.getEstiId());       // ★ 여기서 estiId 세팅
                d.setVersion(vo.getVersion());     // ★ 여기서 version 세팅

                estiSoMapper.insertEstiDetail(d);

                System.out.println("DETAIL AFTER INSERT >>> detailNo="
                        + d.getEstiDetailNo() + ", estiId=" + d.getEstiId()
                        + ", version=" + d.getVersion());
            }
        }

        return vo.getEstiId();
    }
    
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

    @Override
    public String saveOrderFromEsti(EstiSoVO vo) {

        // 1) 주문번호 생성
        String soId = estiSoMapper.createSoId();
        vo.setSoId(soId);

        // 2) 합계 계산
        Long ttSupply = 0L;
        Long ttQty = 0L;
        long ttVat    = 0L;

        if (vo.getDetailList() != null) {
        	for (EstiSoDetailVO d : vo.getDetailList()) {
        	    long supply = d.getSupplyAmt() == null ? 0L : d.getSupplyAmt();
        	    long qy     = d.getQy()         == null ? 0L : d.getQy();

        	    ttSupply += supply;
        	    ttQty    += qy;

        	    // 부가세 10% (소수점은 버림 기준 예시)
        	    ttVat    += supply / 10;   // 10% = supply * 0.1
        	}

        	vo.setTtSupplyPrice(ttSupply);
        	vo.setTtSurtaxPrice(ttVat);           // tb_so.TT_SURTAX_PRICE
        	vo.setTtPrice(ttSupply + ttVat);      // tb_so.TT_PRICE
        	vo.setTtSoQy(ttQty);
        }

        vo.setTtSupplyPrice(ttSupply);
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
        
        // 5) 견적 상태 es4(예: 주문완료)로 변경
        estiSoMapper.updateEstiStatusToOrdered(vo.getEstiId(), vo.getVersion());
        

        return soId;
    }
    
    // ================================================== 주문서관리
    // 주문서 조회
    @Override
    public List<EstiSoVO> selectSoList(EstiSoVO so) {
        return estiSoMapper.selectSo(so);
    }
}