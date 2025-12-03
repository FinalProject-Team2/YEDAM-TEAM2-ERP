package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.VcatnMapper;
import store.yd2team.insa.service.VcatnService;
import store.yd2team.insa.service.VcatnVO;
@Service
@RequiredArgsConstructor
public class VcatnServiceImpl implements VcatnService{

	private final VcatnMapper vcatnMapper;
	
	@Override
	public List<VcatnVO> vcatnListVo(VcatnVO val) {
		//휴가 다중검색조회 쿼리(사용자 및 관리자 유동쿼리)
		return vcatnMapper.getListVcatnJohoe(val);
	}

}
