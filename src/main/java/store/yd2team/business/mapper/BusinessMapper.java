package store.yd2team.business.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.ChurnStdrVO;
import store.yd2team.business.service.ContactVO;
import store.yd2team.business.service.DemoVO;
import store.yd2team.business.service.LeadVO;
import store.yd2team.business.service.MonthlySalesDTO;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.business.service.churnRiskVO;
@Mapper
public interface BusinessMapper {
	
	//휴면,이탈 기준조회
	List<ChurnStdrVO> getChurnStdrList(ChurnStdrVO churn);
	//휴면,이탈 등록 및 수정
	int initChurnStdrByVend(@Param("vendId") String vendId, @Param("empId") String empId);
	int updateChurnStdrByBizKey(ChurnStdrVO vo);
	//휴면,이탈고객 검색조회
	List<churnRiskVO> getchurnRiskList(churnRiskVO vo);
	//휴면, 이탈고객 평균
	int getAVG();
	//휴면, 이탈 조건별 점수화
	List<MonthlySalesDTO> getMonthlySalesChange(MonthlySalesDTO vo);
	//
	//
	//잠재 전체조회
	List<BusinessVO> getList();
	//잠재 조건상세목록 조회
   List<PotentialStdrVO> getPotentialStdrDetailList(PotentialStdrVO cond);

	    // 인서트
	    int insertPotentialStdrDetail(PotentialStdrVO vo);
	    int initPotentialStdrByVend(@Param("vendId") String vendId, @Param("empId") String empId);
	    // 업데이트
	    int updatePotentialStdrDetail(PotentialStdrVO vo);
	    // 잠재고객 기준 상세 삭제
	    int deletePotentialStdr(PotentialStdrVO vo);
	//
	//
	//
	//
	//공공데이터 한 건 insert
   int insertPotential(BusinessVO vo);
   //
 
   //
   //공공데이터 고객 건수 카운트
   int existsPotentialInfoNo(@Param("potentialInfoNo") Long potentialInfoNo);
  
   //잠재고객검색조회
   public List<BusinessVO> getBusinessList(BusinessVO vo);
  
   //로그인한 거래처의 주소, 업체명 조회
   public List<BusinessVO> getcustaddrtype(String info);
   //
   //
	//접촉사항조회
   List<ContactVO> getAction();
   //리드분석조회
   List<ContactVO> getLeadGenerar();
   //잠재고객항목 선택시 해당접촉내역 조회
   List<ContactVO> selectContactListByVend(ContactVO cond);
	    int insertContact(ContactVO vo);
	    int updateContact(ContactVO vo);
   //잠재고객항목 선택시 해당리드내역 조회
   List<LeadVO> getLeadListByVend(LeadVO cond);
	    int insertLead(LeadVO vo);
	    int updateLead(LeadVO vo);
	//잠재고객항목 선택시 해당데모내역 조회
	List<DemoVO> getDemoListByVend(DemoVO cond);
		int insertDemo(DemoVO vo);
	    int updateDemo(DemoVO vo);
}


