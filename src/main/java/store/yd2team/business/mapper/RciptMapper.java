package store.yd2team.business.mapper;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.RciptVO;

@Mapper
public interface RciptMapper {
	//검색조건(조회)
	List<RciptVO> selectRciptList(RciptVO searchVO);
	
	// 입금처리
	void callRciptPayment(Map<String, Object> param);
	
	// 입금 상세내역
	List<RciptVO> selectRciptDetailList(String rciptId);
}