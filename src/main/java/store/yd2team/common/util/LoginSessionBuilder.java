package store.yd2team.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.mapper.MenuAuthMapper;
import store.yd2team.common.service.EmpAcctVO;

@Component
@RequiredArgsConstructor
public class LoginSessionBuilder {

    private final MenuAuthMapper menuAuthMapper; // 사원 메뉴권한
    private final EmpLoginMapper empLoginMapper; // 운영자 판별 + 운영자 메뉴 전체허용

    public SessionDto build(EmpAcctVO empAcct) {

        SessionDto session = new SessionDto();
        session.setEmpAcctId(empAcct.getEmpAcctId());
        session.setVendId(empAcct.getVendId());
        session.setEmpId(empAcct.getEmpId());
        session.setLoginId(empAcct.getLoginId());
        session.setEmpNm(empAcct.getEmpNm());
        session.setDeptId(empAcct.getDeptId());
        session.setDeptNm(empAcct.getDeptNm());
        session.setMasYn(empAcct.getMasYn());
        session.setBizcnd(empAcct.getBizcnd());
        session.setAddr(empAcct.getAddr());
        session.setCttpc(empAcct.getCttpc());
        session.setHp(empAcct.getHp());
        session.setTempYn(empAcct.getTempYn());
        session.setEmail(empAcct.getEmail());
        session.setProofPhoto(empAcct.getProofPhoto());
        session.setAcctSt(empAcct.getSt());
        session.setJobNm(empAcct.getJobNm());

        if (isOperator(empAcct.getLoginId())) {
            buildOperatorSession(session);     // ✅ 여기서 운영자 세션 완성
        } else {
            buildEmployeeSession(session, empAcct); // ✅ 여기서 사원 세션 완성
        }

        return session;
    }

    private boolean isOperator(String loginId) {
        if (loginId == null || loginId.isBlank()) return false;
        return empLoginMapper.countByLoginId(loginId) > 0;
    }

    private void buildOperatorSession(SessionDto session) {
        session.setOprtrYn("e1");

        // 운영자는 역할 개념 없음(임시로 고정)
        session.setRoleIds(List.of("OPERATOR"));
        session.setRoleId("ROLE_OPERATOR");

        // 메뉴 전체 허용
        List<MenuAuthDto> menuList = empLoginMapper.selectAllActiveMenus();

        Map<String, MenuAuthDto> menuMap = new HashMap<>();
        Map<String, MenuAuthDto> menuByUrl = new HashMap<>();

        for (MenuAuthDto m : menuList) {
            menuMap.put(m.getMenuId(), m);
            if (m.getMenuUrl() != null && !m.getMenuUrl().isBlank()) {
                menuByUrl.put(UrlUtil.normalize(m.getMenuUrl()), m);
            }
        }

        session.setMenuAuthMap(menuMap);
        session.setMenuAuthByUrl(menuByUrl);
    }

    private void buildEmployeeSession(SessionDto session, EmpAcctVO empAcct) {
        session.setOprtrYn("e2");

        // roleIds/authCodes는 EmpLoginServiceImpl에서 채워서 EmpAcctVO에 넣어줬다는 전제
        session.setRoleIds(empAcct.getRoleIds());
        session.setAuthCodes(empAcct.getAuthCodes());

        // 기존 단일 roleId(마스터 여부 기반) 로직 유지
        String roleId = "ROLE_USER";
        if ("e1".equals(empAcct.getMasYn())) roleId = "ROLE_HR_ADMIN";
        session.setRoleId(roleId);

        // 사원 메뉴 권한 맵
        List<MenuAuthDto> menuAuthList =
            menuAuthMapper.selectMenuAuthByEmpAcct(empAcct.getEmpAcctId(), empAcct.getVendId(), empAcct.getLoginId());

        Map<String, MenuAuthDto> menuAuthMap = new HashMap<>();
        Map<String, MenuAuthDto> menuAuthByUrl = new HashMap<>();
        for (MenuAuthDto dto : menuAuthList) {
            menuAuthMap.put(dto.getMenuId(), dto);
            if (dto.getMenuUrl() != null && !dto.getMenuUrl().isBlank()) {
                menuAuthByUrl.put(UrlUtil.normalize(dto.getMenuUrl()), dto);
            }
        }
        session.setMenuAuthMap(menuAuthMap);
        session.setMenuAuthByUrl(menuAuthByUrl);
    }
}

