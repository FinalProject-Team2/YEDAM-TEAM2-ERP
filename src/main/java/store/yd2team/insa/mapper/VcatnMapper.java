package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.insa.service.VcatnVO;

@Mapper
public interface VcatnMapper {

	//다중조건 조회
			List<VcatnVO> getListVcatnJohoe(VcatnVO keyword);
}
