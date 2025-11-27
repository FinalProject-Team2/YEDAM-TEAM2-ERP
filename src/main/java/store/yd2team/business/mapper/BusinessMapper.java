package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.BusinessVO;

@Mapper
public interface BusinessMapper {
		
	//전체조회
	List<BusinessVO> getList();
	
	// 공공데이터 한 건 insert
    int insertPotential(BusinessVO vo);
	
	//등록
	int insert(BusinessVO business);
	
	//수정
	
	//단건조회
}
