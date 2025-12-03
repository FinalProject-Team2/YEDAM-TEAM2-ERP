package store.yd2team.business.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import store.yd2team.business.service.BusinessVO;
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
    //휴면, 이탈 매출변동
    List<Map<String, Object>> getSalesChange();
	
	//단건조회
}
