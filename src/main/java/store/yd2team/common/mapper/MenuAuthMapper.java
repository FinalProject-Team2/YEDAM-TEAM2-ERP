package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.MenuAuthDto;

@Mapper
public interface MenuAuthMapper {
	
    List<MenuAuthDto> selectMenuAuthByEmpAcct(
            @Param("empAcctId") String empAcctId,
            @Param("vendId") String vendId);

}
