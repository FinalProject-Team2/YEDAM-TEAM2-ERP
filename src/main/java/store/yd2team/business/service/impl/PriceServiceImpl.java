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

    @Override
    public List<PriceVO> getPricePolicyList(PriceVO vo) {
        return priceMapper.selectPolicy(vo);
    }
    
    @Override
    public List<CommonCodeVO> getPriceType() {
        return priceMapper.selectPriceType();
    }
}
