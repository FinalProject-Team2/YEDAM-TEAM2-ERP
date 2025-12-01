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

    @Override
    public List<EstiSoVO> selectEstiList(EstiSoVO cond) {
        // 필요하면 여기서 기본값 세팅, 검증, 로깅 등 수행
        return estiSoMapper.selectEsti(cond);
    }
    
    @Override
    public int updateStatus(EstiSoVO vo) {
        return estiSoMapper.updateStatus(vo);
    }
}
