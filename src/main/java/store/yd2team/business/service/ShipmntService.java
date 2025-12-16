package store.yd2team.business.service;

import java.util.List;

public interface ShipmntService {
	
	// 출하검색조건(조회) 
	List<ShipmntVO> selectShipmntList(ShipmntVO vo);
	
}
