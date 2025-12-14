package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.business.service.CustcomVO;

@Mapper
public interface CustcomMapper {

	// 조회
	List<CustcomVO> searchCustcom(CustcomVO searchVO);
	 
	// 공통코드 조회
	List<CustcomVO> selectBSType();
	 
	// 저장 
	int insertCustcom(CustcomVO vo);
	
	// 수정
	int updateCustcom(CustcomVO vo);
	
	// 여신에 insert
	void insertCreditLimit(
	    @Param("custcomId") String custcomId,
	    @Param("vendId") String vendId
	);
	 
}
