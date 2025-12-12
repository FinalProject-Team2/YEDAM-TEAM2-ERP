package store.yd2team.common.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.aop.SysLog;
import store.yd2team.common.aop.SysLogConfig;
import store.yd2team.common.dto.AuthSaveResult;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.RoleSaveRequest;
import store.yd2team.common.dto.RoleSaveResult;
import store.yd2team.common.mapper.AuthManageMapper;
import store.yd2team.common.service.AuthManageService;
import store.yd2team.common.service.RoleVO;

@SysLogConfig(module = "cm", table = "TB_ROLE", pkParam = "roleId")
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthManageServiceImpl implements AuthManageService {
	
	private final AuthManageMapper authManageMapper;

    @Override
    public List<RoleVO> getRoleList(String vendId,
                                    String roleNm,
                                    String roleTy,
                                    String useYn) {

        log.debug("getRoleList vendId={}, roleNm={}, roleTy={}, useYn={}",
                  vendId, roleNm, roleTy, useYn);

        return authManageMapper.selectRoleList(vendId, roleNm, roleTy, useYn);
    }

    @Override
    public List<MenuAuthDto> getMenuAuthByRoleAndModule(String vendId,
                                                        String roleId,
                                                        String moduleId) {

        log.debug("getMenuAuthByRoleAndModule vendId={}, roleId={}, moduleId={}",
                  vendId, roleId, moduleId);

        return authManageMapper.selectMenuAuthByRoleAndModule(vendId, roleId, moduleId);
    }
    
    @Override
    public List<MenuAuthDto> getMenuAuthList(String vendId, String roleId, String moduleId) {
        return authManageMapper.selectMenuAuthByRoleAndModule(vendId, roleId, moduleId);
    }
    
    @Transactional
    @Override
    @SysLog(action = "au1", msg = "메뉴 권한 저장", pkFromSession = false, pkField = "roleId")
    public AuthSaveResult saveMenuAuth(String vendId,
			                           String roleId,
			                           List<MenuAuthDto> authList,
			                           String empId) {

    	AuthSaveResult result = new AuthSaveResult();
        result.setFirstSave(false);
        result.setAffectedCount(0);

        if (authList == null || authList.isEmpty()) {
            return result;
        }

        // 저장 전, 이 역할에 대한 권한이 DB에 이미 있는지 확인
        boolean existedBefore = authManageMapper.countAuthByRole(vendId, roleId) > 0;

        int total = 0;
        for (MenuAuthDto item : authList) {
            int cnt = authManageMapper.mergeMenuAuth(vendId, roleId, empId, item);
            total += cnt;
        }

        result.setFirstSave(!existedBefore);
        result.setAffectedCount(total);
        return result;
    }
    
    
    @Override
    @SysLog(action = "ro1", msg = "역할 저장(생성/수정)", pkFromSession = false, pkField = "")
    public RoleSaveResult saveRoleList(String vendId,
                             String empId,
                             RoleSaveRequest req) {

    	RoleSaveResult result = new RoleSaveResult(0, 0);
    	
        if (req == null) {
            return result;
        }

     // 1) 생성
        if (req.getCreated() != null) {
            for (RoleVO role : req.getCreated()) {
                role.setVendId(vendId);
                role.setCreaBy(empId);
                role.setUpdtBy(empId);

                int cnt = authManageMapper.insertRole(role); // 1 또는 0
                result.setCreatedCount(result.getCreatedCount() + cnt);
            }
        }

        // 2) 수정
        if (req.getUpdated() != null) {
            for (RoleVO role : req.getUpdated()) {
                role.setVendId(vendId);
                role.setUpdtBy(empId);

                int cnt = authManageMapper.updateRole(role);
                result.setUpdatedCount(result.getUpdatedCount() + cnt);
            }
        }

        return result;

    }
    
    @Override
    public List<RoleVO> getRoleNameSuggestList(String vendId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return authManageMapper.selectRoleNameSuggestList(vendId, keyword);
    }
}
