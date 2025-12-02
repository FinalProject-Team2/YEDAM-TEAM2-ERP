package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.CustcomVO;
import store.yd2team.business.service.PriceVO;

@Mapper
public interface CustcomMapper {

	// 조회
	 List<CustcomVO> searchCustcom(CustcomVO searchVO);
	 
	 // 공통코드 조회
	 List<CustcomVO> selectBSType();
	 
	// 저장 
	 int insertCustcom(CustcomVO vo);
	 
}
