package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.OustMapper;
import store.yd2team.business.service.OustService;
import store.yd2team.business.service.OustVO;

@Service
@RequiredArgsConstructor
public class OustServiceImpl implements OustService {

	private final OustMapper oustMapper;
	
	// 출하지시서 조회
	@Override
    public List<OustVO> selectOust(OustVO vo) {
        return oustMapper.selectOust(vo);
    }
}
