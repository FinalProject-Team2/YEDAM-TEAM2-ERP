package store.yd2team.business.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.RciptVO;

@Mapper
public interface RciptMapper {
	//검색조건(조회)
	List<RciptVO> selectRciptList(RciptVO searchVO);
	
	//검색조건(저장)
	int insertCredit(RciptVO vo);
	
	// 조회 고객사 auto complete(고객코드, 고객사명)
	List<RciptVO> searchCustcomId(String keyword);    // 고객사코드(아이디) 검색
	List<RciptVO> searchCustcomName(String keyword);  // 고객사명 검색
}