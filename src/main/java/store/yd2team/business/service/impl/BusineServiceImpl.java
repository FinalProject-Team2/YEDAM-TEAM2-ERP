package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.BusinessMapper;
import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.BusinessVO;

@Service
@RequiredArgsConstructor
public class BusineServiceImpl implements BusinessService {
	
	private final BusinessMapper businessMapper;
	
	@Override
	public List<BusinessVO> getList() {
		return businessMapper.getList();
	}

}
