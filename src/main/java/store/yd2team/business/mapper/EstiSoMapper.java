package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.business.service.EstiSoDetailVO;
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
    
    // 모달 고객사 auto complete
    List<EstiSoVO> searchCustcomName(String keyword);  // 고객사명 검색
    List<EstiSoVO> searchCustcomId(String keyword);    // 고객사코드(아이디) 검색
    
    
    // 모달 저장버튼
    // 헤더 저장 (항상 INSERT)
    int insertEsti(EstiSoVO vo);

    // 상세 저장 (항상 INSERT)
    int insertEstiDetail(EstiSoDetailVO detail);

    // 현재 버전 조회 (없으면 0)
    String selectCurrentVersion(String estiId);

    // 조회용
    EstiSoVO selectEstiHeader(String estiId);

    List<EstiSoDetailVO> selectEstiDetailList(String estiId);
}
