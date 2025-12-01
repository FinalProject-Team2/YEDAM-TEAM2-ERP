package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.EstiSoVO;

@Mapper
public interface EstiSoMapper {

	 // 견적서 목록 조회
    List<EstiSoVO> selectEsti(EstiSoVO cond);
    
    int updateStatus(EstiSoVO vo);
}
