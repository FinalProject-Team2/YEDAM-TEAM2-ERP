package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.OustVO;

@Mapper
public interface OustMapper {

	// 출하지시서 조회
    List<OustVO> selectOust(OustVO vo);

}
