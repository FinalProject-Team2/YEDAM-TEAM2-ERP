package store.yd2team.business.service;

import java.util.List;

public interface BusinessService {

	List<BusinessVO> getList();
	
	void fetchAndSaveFromApi();
	
	//번호조회
    int existsPotentialInfoNo(Long potentialInfoNo);
}
