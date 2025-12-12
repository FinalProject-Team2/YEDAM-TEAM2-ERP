package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.DeptMapper;
import store.yd2team.insa.service.DeptService;
import store.yd2team.insa.service.DeptVO;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService{

	private final DeptMapper deptMapper;

	@Override
	public List<DeptVO> getListDept(String vendId) {
		
		return deptMapper.getListDept(vendId);
	}

	@Override
	public List<DeptVO> getNonManagerEmployeeIds(String vendId) {
		
		return deptMapper.getNonManagerEmployeeIds(vendId);
	}
}
