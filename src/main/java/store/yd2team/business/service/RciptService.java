package store.yd2team.business.service;

import java.util.List;

public interface RciptService {

	List<RciptVO> searchRcipt(RciptVO searchVO);

	List<RciptVO> searchCustcomId(String keyword);

	List<RciptVO> searchCustcomName(String keyword);


}
