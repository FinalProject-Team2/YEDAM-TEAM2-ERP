package store.yd2team.business.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.RciptMapper;
import store.yd2team.business.service.RciptService;
import store.yd2team.business.service.RciptVO;

@Service
@RequiredArgsConstructor
public class RciptServiceImpl implements RciptService {

	private final RciptMapper rciptMapper;
	
	// 조회
    @Override
    public List<RciptVO> searchRcipt(RciptVO searchVO) {
        return rciptMapper.selectRciptList(searchVO);
    }
    // 조회 고객사 auto complete(고객코드, 고객사명)
    @Override
    public List<RciptVO> searchCustcomId(String keyword) {
        return rciptMapper.searchCustcomId(keyword);
    }
    @Override
    public List<RciptVO> searchCustcomName(String keyword) {
        return rciptMapper.searchCustcomName(keyword);
    }
    //입금내역
	@Override
	public int insertRciptDetail(RciptVO vo) {
		return rciptMapper.insertRciptDetail(vo);
	}
}
