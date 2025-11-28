package store.yd2team.business.service.impl;

import java.util.List;

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
}
