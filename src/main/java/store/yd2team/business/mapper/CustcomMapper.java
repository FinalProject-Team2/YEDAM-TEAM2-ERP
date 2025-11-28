package store.yd2team.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.business.service.CustcomVO;

@Mapper
public interface CustcomMapper {

	 List<CustcomVO> searchCustcom(CustcomVO searchVO);
}
