package store.yd2team.business.service;

import java.util.List;

public interface BusinessService {

	List<BusinessVO> getList();
	
	void fetchAndSaveFromApi();
	
	//검색조건조회
	List<BusinessVO> getBusinessList(BusinessVO vo);
	
	//번호조회
    int existsPotentialInfoNo(Long potentialInfoNo);
    //
    //
    //잠재고객기준상세목록조회
    public List<PotentialStdrVO> getStdrDetailAll();

}
