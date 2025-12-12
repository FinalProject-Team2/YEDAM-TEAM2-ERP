package store.yd2team.business.service;

import java.util.List;

public interface RciptService {

	//조회
	List<RciptVO> searchRcipt(RciptVO searchVO);

	//조회조건 자동완성
	/*
	 * List<RciptVO> searchCustcomId(String keyword); List<RciptVO>
	 * searchCustcomName(String keyword);
	 */

	//입금내역
	int insertRciptDetail(RciptVO vo);
}
