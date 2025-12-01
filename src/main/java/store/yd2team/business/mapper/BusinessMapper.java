package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.PotentialStdrVO;

@Mapper
public interface BusinessMapper {
		
	//전체조회
	List<BusinessVO> getList();
	
	//잠재고객조건상세목록조회
	 List<PotentialStdrVO> getStdrDetailAll();
	
	// 공공데이터 한 건 insert
    int insertPotential(BusinessVO vo);
    
    //번호조회
    int existsPotentialInfoNo(@Param("potentialInfoNo") Long potentialInfoNo);
    
    //잠재고객기준조회
    	
    
    //잠재고객검색조회
    public List<BusinessVO> getBusinessList(BusinessVO vo);
    
    //잠재고객기준등록
    
    //잠재고객기준수정

	
	//등록
//	int insert(BusinessVO business);
	
	//수정
	
	//단건조회
}
