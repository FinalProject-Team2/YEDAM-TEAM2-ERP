package store.yd2team.business.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.PriceMapper;
import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceService;
import store.yd2team.business.service.PriceVO;

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
        int result = priceMapper.savePricePolicy(vo);
        System.out.println("### result = " + result);
        return 1;   // 변화 건수 상관없이 성공 처리
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

        String priceId = vo.getPriceId();

        // 1) 기존 detail 삭제
        priceMapper.deletePriceDetail(priceId);

        // 2) 새 detail 저장
        if (vo.getDetailList() != null && !vo.getDetailList().isEmpty()) {

            int idx = 1;

            for (Map<String, Object> d : vo.getDetailList()) {

                d.put("priceId", priceId);
                d.put("detailNo", idx++);

                // 헤더 공통값 저장
                d.put("applcStartDt", vo.getBeginDt());
                d.put("applcEndDt", vo.getEndDt());
                d.put("dcRate", vo.getPercent());

                priceMapper.insertPriceDetail(d);
            }
        }

        return 1;
    }

    @Override
    public List<Map<String, Object>> selectPricePolicyDetail(String priceId) {
        return priceMapper.selectPriceDetail(priceId);
    }
}
