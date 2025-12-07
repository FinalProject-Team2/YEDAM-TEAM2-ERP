package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.ContactVO;
import store.yd2team.business.service.MonthlySalesDTO;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.business.service.churnRiskVO;

@Mapper
public interface BusinessMapper {
		
	//전체조회
	List<BusinessVO> getList();
	
	//잠재고객조건상세목록조회
	 List<PotentialStdrVO> getStdrDetailAll();
	 
	//잠재고객조건상세목록수정
    List<BusinessVO> selectByCondGb(String condGb);

    int insertDetail(BusinessVO vo);

    int updateDetail(BusinessVO vo);

	// 공공데이터 한 건 insert
    int insertPotential(BusinessVO vo);
    
    //번호조회
    int existsPotentialInfoNo(@Param("potentialInfoNo") Long potentialInfoNo);
    
    //잠재고객검색조회
    public List<BusinessVO> getBusinessList(BusinessVO vo);
    
    //로그인한 거래처의 주소, 업체명 조회
    public List<BusinessVO> getcustaddrtype(String info);
    //
    //
    //휴면,이탈고객 검색조회
    List<churnRiskVO> getchurnRiskList(churnRiskVO vo);
    //
	//휴면, 이탈고객 평균
    int getAVG();
    //휴면, 이탈 조건별 점수화
    List<MonthlySalesDTO> getMonthlySalesChange(MonthlySalesDTO vo);
    //
    //
	//접촉사항조회
    List<ContactVO> getAction();
    //리드분석조회
    List<ContactVO> getLeadGenerar();
    //잠재고객항목 선택시 해당접촉사항 조회
    List<ContactVO> selectContactListByVend(String vendId);
    int deleteContactsByVend(String vendId);
    int insertContact(ContactVO vo);
}



