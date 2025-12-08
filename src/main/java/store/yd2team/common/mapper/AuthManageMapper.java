package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.service.RoleVO;

@Mapper
public interface AuthManageMapper {

	// 역할 목록
    List<RoleVO> selectRoleList(@Param("vendId") String vendId,
                                @Param("roleNm") String roleNm,
                                @Param("roleTy") String roleTy,
                                @Param("useYn")  String useYn);

    // 모듈 + 역할별 메뉴/권한
    List<MenuAuthDto> selectMenuAuthByRoleAndModule(@Param("vendId")   String vendId,
                                                    @Param("roleId")   String roleId,
                                                    @Param("moduleId") String moduleId);
    
    // 권한 저장
    int mergeMenuAuth(@Param("vendId") String vendId,
			          @Param("roleId") String roleId,
			          @Param("empId")  String empId,
			          @Param("item")   MenuAuthDto item);
	
    int insertRole(RoleVO role);
    int updateRole(RoleVO role);
    
    int countAuthByRole(@Param("vendId") String vendId,
            			@Param("roleId") String roleId);
    
    List<RoleVO> selectRoleNameSuggestList(
            @Param("vendId") String vendId,
            @Param("keyword") String keyword
    );
}
