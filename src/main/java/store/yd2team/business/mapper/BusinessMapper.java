package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import store.yd2team.business.service.BusinessVO;

@Mapper
public interface BusinessMapper {
		
	//전체조회
	List<BusinessVO> getList();
	
	//잠재고객조건조회
	List<BusinessVO> getBusinessList(BusinessVO BusinessVO);
	
	// 공공데이터 한 건 insert
    int insertPotential(BusinessVO vo);
    
    //번호조회
    int existsPotentialInfoNo(@Param("potentialInfoNo") Long potentialInfoNo);

	
	//등록
//	int insert(BusinessVO business);
	
	//수정
	
	//단건조회
}
