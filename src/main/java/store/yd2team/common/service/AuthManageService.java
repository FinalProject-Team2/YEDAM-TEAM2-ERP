package store.yd2team.common.service;

import java.util.List;

import store.yd2team.common.dto.AuthSaveResult;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.RoleSaveRequest;
import store.yd2team.common.dto.RoleSaveResult;

public interface AuthManageService {

	List<RoleVO> getRoleList(String vendId,
				             String roleNm,
				             String roleTy,
				             String useYn);
	
	List<MenuAuthDto> getMenuAuthByRoleAndModule(String vendId,
							 		             String roleId,
									             String moduleId);
	
	AuthSaveResult saveMenuAuth(String vendId,
					            String roleId,
					            List<MenuAuthDto> authList,
					            String empId);
	
	RoleSaveResult saveRoleList(String vendId,
		              String empId,
		              RoleSaveRequest req);
	
	List<MenuAuthDto> getMenuAuthList(String vendId, String roleId, String moduleId);
	
	List<RoleVO> getRoleNameSuggestList(String vendId, String keyword);
}
