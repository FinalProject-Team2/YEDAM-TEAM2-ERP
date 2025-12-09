package store.yd2team.common.service;

import java.util.List;

public interface RoleAuthService {

    List<RoleVO> getRoleNameSuggest(String vendId, String keyword);

    List<RoleVO> getRoleList(String vendId,
                             String roleNm,
                             String roleTy,
                             String useYn);

    List<MenuAuthVO> getMenuAuthList(String vendId,
                                     String roleId,
                                     String moduleId);
}
