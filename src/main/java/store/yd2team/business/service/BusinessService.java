package store.yd2team.business.service;
import java.util.List;
public interface BusinessService {
	
	List<BusinessVO> getList();
	
	//공공데이터 잠개고객 데이터 매핑
	void fetchAndSaveFromApi();
	
	//휴면,이탈 기준조회
	List<ChurnStdrVO> getChurnStdrList(ChurnStdrVO churn);
	//휴면,이탈 수정
	int updateChurnStdrList(List<ChurnStdrVO> dormancyList, List<ChurnStdrVO> churnList, String vendId, String empId);
	//검색조건조회
	List<BusinessVO> getBusinessList(BusinessVO vo);
	//번호조회
	int existsPotentialInfoNo(Long potentialInfoNo);
	//
	//
	// 전체 조회 (cond는 안 써도 시그니처는 남김)
	public List<PotentialStdrVO> getPotentialStdrDetailList(PotentialStdrVO cond);
	//잠재고객 기준상세목록 등록 및 수정
	public void savePotentialStdrDetailList(List<PotentialStdrVO> list);
	//잠재고객 기준상세목록 삭제
	public int deletePotentialStdrList(List<String> idList);
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
	//
	//접촉사항 조회
	List<ContactVO> getAction();
	//잠재고객항목 선택시 해당 접촉내역 조회
	public List<ContactVO> getContactListByVend(Integer potentialInfoNo);
	//접촉사항 내용 저장
	void saveAll(String vendId, Integer potentialInfoNo, List<ContactVO> contactList);
	//잠재고객항목 선택시 해당 리드내역 조회 및 저장
	public List<LeadVO> getLeadListByVend(Integer potentialInfoNo);
	public void saveAllLead(String vendId, Integer potentialInfoNo, List<LeadVO> leadList);
	//잠재고객항목 선택시 해당 데모내역 조회 및 저장
	public List<DemoVO> getDemoListByVend(Integer potentialInfoNo);
	public void saveAllDemo(String vendId, Integer potentialInfoNo, List<DemoVO> demoList);

}

