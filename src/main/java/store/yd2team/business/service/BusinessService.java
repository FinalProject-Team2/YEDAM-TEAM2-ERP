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
    
    //잠재고객기준상세목록수정
    List<BusinessVO> getListByCond(String condGb);
    
    public void saveAll(List<BusinessVO> list);
    
    //로그인한 거래처의 주소, 업체명 조회
    public List<BusinessVO> getcustaddrtype(String info);
    //
    //
    //휴면,이탈고객 검색조회
    List<churnRiskVO> getchurnRiskList(churnRiskVO vo);
    //휴면,이탈 평균구매주기
    int getAVG();

}
