package store.yd2team.business.service;

import java.util.List;

public interface EstiSoService {

	// 견적서 목록 조회
    List<EstiSoVO> selectEstiList(EstiSoVO cond);
    
    int updateStatus(EstiSoVO vo);
}
