package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceVO;

@Mapper
public interface PriceMapper {

	// 조회
	List<PriceVO> selectPolicy(PriceVO searchVO);
	
	 // 공통코드 조회
    List<CommonCodeVO> selectPriceType();
    
    // 등록 및 수정
    int savePricePolicy(PriceVO vo);
    
    // 삭제
    void deletePricePolicy(List<String> priceIdList);
}
