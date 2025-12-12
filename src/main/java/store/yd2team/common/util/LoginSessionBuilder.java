package store.yd2team.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.MenuAuthMapper;
import store.yd2team.common.service.EmpAcctVO;

@Component
@RequiredArgsConstructor
public class LoginSessionBuilder {

    private final MenuAuthMapper menuAuthMapper;

    public SessionDto build(EmpAcctVO empAcct) {

        SessionDto loginEmp = new SessionDto();
        loginEmp.setEmpAcctId(empAcct.getEmpAcctId());
        loginEmp.setVendId(empAcct.getVendId());
        loginEmp.setEmpId(empAcct.getEmpId());
        loginEmp.setLoginId(empAcct.getLoginId());
        loginEmp.setEmpNm(empAcct.getEmpNm());
        loginEmp.setDeptId(empAcct.getDeptId());
        loginEmp.setDeptNm(empAcct.getDeptNm());
        loginEmp.setMasYn(empAcct.getMasYn());
        loginEmp.setBizcnd(empAcct.getBizcnd());
        loginEmp.setAddr(empAcct.getAddr());
        loginEmp.setCttpc(empAcct.getCttpc());
        loginEmp.setHp(empAcct.getHp());
        loginEmp.setTempYn(empAcct.getTempYn());
        loginEmp.setRoleIds(empAcct.getRoleIds());
        loginEmp.setAuthCodes(empAcct.getAuthCodes());
        loginEmp.setEmail(empAcct.getEmail());
        loginEmp.setProofPhoto(empAcct.getProofPhoto());

        // ✅ (너가 추가할 예정인) 계정상태도 같이 담기
        // SessionDto에 acctSt 필드 추가해둔 상태라고 가정
        loginEmp.setAcctSt(empAcct.getSt());

        // roleId 단일값 세팅(기존 로직 유지)
        String roleId = "ROLE_USER";
        if ("e1".equals(empAcct.getMasYn())) {
            roleId = "ROLE_HR_ADMIN";
        }
        loginEmp.setRoleId(roleId);

        // 메뉴 권한 맵 구성
        List<MenuAuthDto> menuAuthList =
                menuAuthMapper.selectMenuAuthByEmpAcct(empAcct.getEmpAcctId(), empAcct.getVendId());

        Map<String, MenuAuthDto> menuAuthMap = new HashMap<>();
        for (MenuAuthDto dto : menuAuthList) {
            menuAuthMap.put(dto.getMenuId(), dto);
        }
        loginEmp.setMenuAuthMap(menuAuthMap);

        return loginEmp;
    }
}
