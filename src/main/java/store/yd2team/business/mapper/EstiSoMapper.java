package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.business.service.EstiSoVO;

@Mapper
public interface EstiSoMapper {

	 // 견적서 목록 조회
    List<EstiSoVO> selectEsti(EstiSoVO cond);
    
    // 견적서 그리드 상태 업데이트
    int updateStatus(EstiSoVO vo);
    
    // 견적서 모달 상품 auto complete
    List<EstiSoVO> searchProduct(@Param("keyword") String keyword);
    EstiSoVO getProductDetail(@Param("productId") String productId);
}
