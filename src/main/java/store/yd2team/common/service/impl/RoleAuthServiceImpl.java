package store.yd2team.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.mapper.RoleAuthMapper;
import store.yd2team.common.service.MenuAuthVO;
import store.yd2team.common.service.RoleAuthService;
import store.yd2team.common.service.RoleVO;

@Service
@RequiredArgsConstructor
public class RoleAuthServiceImpl implements RoleAuthService {

    private final RoleAuthMapper roleAuthMapper;

    private String emptyToNull(String s) {
        return (s != null && s.trim().isEmpty()) ? null : s;
    }

    @Override
    public List<RoleVO> getRoleNameSuggest(String vendId, String keyword) {
        return roleAuthMapper.selectRoleNameSuggest(vendId, emptyToNull(keyword));
    }

    @Override
    public List<RoleVO> getRoleList(String vendId,
                                    String roleNm,
                                    String roleTy,
                                    String useYn) {

        return roleAuthMapper.selectRoleList(
                vendId,
                emptyToNull(roleNm),
                emptyToNull(roleTy),
                emptyToNull(useYn)
        );
    }

    @Override
    public List<MenuAuthVO> getMenuAuthList(String vendId,
                                            String roleId,
                                            String moduleId) {

        return roleAuthMapper.selectMenuAuthList(
                vendId,
                emptyToNull(roleId),
                emptyToNull(moduleId)
        );
    }
}
