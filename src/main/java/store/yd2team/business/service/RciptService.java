package store.yd2team.business.service;

import java.util.List;

public interface RciptService {

	//조회
	List<RciptVO> searchRcipt(RciptVO searchVO);

	// 입금처리
	void saveRcipt(RciptVO vo);
	
	// 입금 상세내역
	List<RciptVO> selectRciptDetailList(String rciptId);
}
