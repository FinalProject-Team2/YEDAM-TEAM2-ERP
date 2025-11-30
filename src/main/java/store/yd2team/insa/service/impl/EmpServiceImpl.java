package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.EmpMapper;
import store.yd2team.insa.service.EmpService;
import store.yd2team.insa.service.EmpVO;

@Service
@RequiredArgsConstructor
public class EmpServiceImpl implements EmpService{
	
	private final EmpMapper empMapper;
	
	@Override
	public List<EmpVO> getListEmpJohoe(EmpVO emp) {
		
		return empMapper.getListEmpJohoe();
	}

	@Override
	public int setDbEdit(EmpVO emp) {
		
		return empMapper.setDbEdit(emp);
	}

	@Override
	public EmpVO setDbAddId() {
		
		return empMapper.setDbAddId();
	}
	
	@Override
	public int setDbAdd(EmpVO emp) {
		
		return empMapper.setDbAdd(emp);
	}

}
