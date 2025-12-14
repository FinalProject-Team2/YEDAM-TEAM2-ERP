package store.yd2team.business.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.PriceMapper;
import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceService;
import store.yd2team.business.service.PriceVO;
import store.yd2team.common.util.LoginSession;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

	private final PriceMapper priceMapper;

	// 조회
    @Override
    public List<PriceVO> getPricePolicyList(PriceVO vo) {
        return priceMapper.selectPolicy(vo);
    }
    
    // 공통코드 정책유형
    @Override
    public List<CommonCodeVO> getPriceType() {
        return priceMapper.selectPriceType();
    }

    // 등록 및 수정
    @Override
    public int savePricePolicy(PriceVO vo) throws Exception {
        System.out.println("### Service savePricePolicy 호출 ###");

        // 로그인 세션 정보
        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();  // 등록자 / 수정자

        // MERGE 문에서 사용할 값 세팅
        vo.setVendId(vendId);
        vo.setCreaBy(empId);
        vo.setUpdtBy(empId);

        int result = priceMapper.savePricePolicy(vo);

        System.out.println("### result = " + result);
        return 1;   // 성공 처리
    }
    
    // 삭제
    @Override
    public void deletePricePolicy(List<String> priceIdList) throws Exception {
        priceMapper.deletePricePolicy(priceIdList);
    }
    
    // 고객사모달 저장버튼 이벤트
    @Override
    public int savePricePolicyDetail(PriceVO vo) {

        if (vo.getPriceId() == null || vo.getPriceId().trim().isEmpty()) {
            throw new RuntimeException("PRICE_POLICY_ID 가 없습니다.");
        }

        if (vo.getDetailList() == null || vo.getDetailList().isEmpty()) {
            throw new RuntimeException("고객사 detailList 가 비어있습니다.");
        }

        String priceId = vo.getPriceId();
        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        // 1) 기존 고객사 detail 삭제
        priceMapper.deletePriceDetail(priceId);

        // 2) 새 고객사 detail 저장
        for (Map<String, Object> d : vo.getDetailList()) {

            d.put("priceId", priceId);

            // 적용 정보
            d.put("applcStartDt", vo.getBeginDt());
            d.put("applcEndDt", vo.getEndDt());
            d.put("dcRate", vo.getPercent());

            // 🔥 세션 정보
            d.put("vendId", vendId);
            d.put("creaBy", empId);
            d.put("updtBy", empId);

            priceMapper.insertPriceDetail(d);
        }
		/*
		 * int idx = 1; for (Map<String, Object> d : vo.getDetailList()) {
		 * 
		 * d.put("priceId", priceId); d.put("detailNo", idx++); // 고객사 detail 번호
		 * 
		 * // 공통 헤더값 d.put("applcStartDt", vo.getBeginDt()); d.put("applcEndDt",
		 * vo.getEndDt()); d.put("dcRate", vo.getPercent());
		 * 
		 * // 🔥 세션 기반 컬럼 d.put("vendId", vendId); d.put("creaBy", empId);
		 * d.put("updtBy", empId);
		 * 
		 * priceMapper.insertPriceDetail(d); }
		 */

        return 1;
    }

    
    @Override
    public List<Map<String, Object>> selectPricePolicyDetail(String priceId) {

        List<Map<String, Object>> list = priceMapper.selectPricePolicyDetail(priceId);

        System.out.println("=== [DEBUG Service] list = " + list);

        return list;
    } // 고객사모달 end
    
    // ====================================== 상품모달
    // 조회
    @Override
    public List<Map<String, Object>> selectProductList(String productName) {
        return priceMapper.selectProductList(productName);
    }
    
    // 저장
    @Override
    public List<Map<String, Object>> selectPricePolicyProduct(String priceId) {
        return priceMapper.selectPricePolicyProduct(priceId);
    }

    
    // 상품 디테일 저장
    @Override
    public int savePricePolicyProduct(PriceVO vo) {

        if (vo.getPriceId() == null || vo.getPriceId().trim().isEmpty()) {
            throw new RuntimeException("PRICE_POLICY_ID 가 없습니다.");
        }

        String priceId = vo.getPriceId();
        String vendId = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        // 1) 기존 상품 detail 삭제
        priceMapper.deletePriceProductDetail(priceId);

        // 2) 새 상품 detail 저장
        if (vo.getDetailList() != null && !vo.getDetailList().isEmpty()) {

            for (Map<String, Object> d : vo.getDetailList()) {

                d.put("priceId", priceId);

                // 공통 헤더값
                d.put("applcStartDt", vo.getBeginDt());
                d.put("applcEndDt", vo.getEndDt());
                d.put("dcRate", vo.getPercent());

                // 🔥 세션 정보
                d.put("vendId", vendId);
                d.put("creaBy", empId);
                d.put("updtBy", empId);

                priceMapper.insertPriceProductDetail(d);
            }
        }

        return 1;
    }
    
    // 최대 할인율
    @Override
    public PriceVO getMaxDiscount(PriceVO vo) {
        return priceMapper.selectMaxDiscount(vo);
    }
}
