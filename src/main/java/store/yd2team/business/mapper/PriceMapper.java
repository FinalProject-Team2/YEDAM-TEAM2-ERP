package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceVO;

@Mapper
public interface PriceMapper {

	List<PriceVO> selectPolicy(PriceVO searchVO);
	
	 // 공통코드 조회
    List<CommonCodeVO> selectPriceType();
}
