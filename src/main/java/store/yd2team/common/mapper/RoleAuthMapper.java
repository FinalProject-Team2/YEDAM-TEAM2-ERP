package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.service.MenuAuthVO;
import store.yd2team.common.service.RoleVO;

@Mapper
public interface RoleAuthMapper {

    // 자동완성 (역할 명)
    List<RoleVO> selectRoleNameSuggest(@Param("vendId") String vendId,
                                       @Param("keyword") String keyword);

    // 역할 목록 조회 (모달/페이지 공통)
    List<RoleVO> selectRoleList(@Param("vendId") String vendId,
                                @Param("roleNm") String roleNm,
                                @Param("roleTy") String roleTy,
                                @Param("useYn") String useYn);

    // 권한 목록 조회 (moduleId + roleId 기준)
    List<MenuAuthVO> selectMenuAuthList(@Param("vendId") String vendId,
                                        @Param("roleId") String roleId,
                                        @Param("moduleId") String moduleId);
}
