package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.ShipmntVO;

@Mapper
public interface ShipmntMapper {
	
	// 견적서 목록 조회
    List<ShipmntVO> selectShipmnt(ShipmntVO cond);
	
}
