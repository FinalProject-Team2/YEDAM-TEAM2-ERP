package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.AtmptMapper;
import store.yd2team.business.service.AtmptService;
import store.yd2team.business.service.AtmptVO;
import store.yd2team.business.service.CreditVO;

@Service
@RequiredArgsConstructor
public class AtmptServiceImpl implements AtmptService {

	private final AtmptMapper atmptMapper;
	
	// 조회
    @Override
    public List<AtmptVO> searchAtmpt(AtmptVO searchVO) {
        return atmptMapper.selectAtmpt(searchVO);
    }
    // 조회 고객사 auto complete(고객코드, 고객사명)
    @Override
    public List<AtmptVO> searchCustcomId(String keyword) {
        return atmptMapper.searchCustcomId(keyword);
    }
    @Override
    public List<AtmptVO> searchCustcomName(String keyword) {
        return atmptMapper.searchCustcomName(keyword);
    }
}
