package store.yd2team.business.service;

import java.util.List;

public interface PriceService {

	List<PriceVO> getPricePolicyList(PriceVO vo);
	
	// 공통코드 조회
    List<CommonCodeVO> getPriceType();
}
