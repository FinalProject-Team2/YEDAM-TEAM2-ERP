package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.EstiSoMapper;
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

}
