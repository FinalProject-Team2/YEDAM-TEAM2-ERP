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
   //모든조건 점수화
   List<MonthlySalesDTO> getMonthlySalesChange(MonthlySalesDTO vo);
   //
   //
   //접촉사항 조회
   List<ContactVO> getAction();
   //잠재고객항목 선택시 해당 접촉내역 조회
   public List<ContactVO> getContactListByVend(String vendId);
   //접촉사항 내용 저장
   public void saveAll(String vendId, Integer potentialInfoNo, List<ContactVO> contactList);
   //잠재고객항목 선택시 해당 리드내역 조회
   List<LeadVO> getLeadListByVend(String vendId);
   public void saveAllLead(String vendId, Integer potentialInfoNo, List<LeadVO> leadList);
   //잠재고객항목 선택시 해당 데모내역 조회
   List<DemoVO> getDemoListByVend(String vendId);
   public void saveAllDemo(String vendId, Integer potentialInfoNo, List<DemoVO> demoList);

}


