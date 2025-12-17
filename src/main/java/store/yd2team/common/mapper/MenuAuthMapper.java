package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.MenuAuthDto;

@Mapper
public interface MenuAuthMapper {
	
    List<MenuAuthDto> selectMenuAuthByEmpAcct(
            @Param("empAcctId") String empAcctId,
            @Param("vendId") String vendIdm,
            @Param("loginId") String loginId);
    
    List<MenuAuthDto> selectAllMenusForOprtr();
    
    List<MenuAuthDto> selectMenusForUnpaid(@Param("vendId") String vendId);
    
    String selectMenuNameByUrl(@Param("vendId") String vendId,
            				   @Param("menuUrl") String menuUrl);
    
}
