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
	
	// 견적서 조회
    @Override
    public List<OustVO> getOustList(OustVO cond) {
        return oustMapper.selectOust(cond);
    }
}
