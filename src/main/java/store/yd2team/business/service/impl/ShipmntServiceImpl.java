package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.ShipmntMapper;
import store.yd2team.business.service.ShipmntService;
import store.yd2team.business.service.ShipmntVO;

@Service
@RequiredArgsConstructor
public class ShipmntServiceImpl implements ShipmntService {
	
	private final ShipmntMapper shipmntMapper;

	// 견적서 조회
    @Override
    public List<ShipmntVO> selectShipmntList(ShipmntVO cond) {
        return shipmntMapper.selectShipmnt(cond);
    }
}
