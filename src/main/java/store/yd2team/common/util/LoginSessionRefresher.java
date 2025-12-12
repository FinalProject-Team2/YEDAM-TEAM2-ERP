package store.yd2team.common.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.mapper.EmpLoginMapper;
import store.yd2team.common.service.EmpAcctVO;

@Component
@RequiredArgsConstructor
public class LoginSessionRefresher {

    private final EmpLoginMapper empLoginMapper;
    private final LoginSessionBuilder loginSessionBuilder;

    public void refresh(HttpSession session, String empAcctId) {
        if (session == null) return;

        EmpAcctVO empAcct = empLoginMapper.selectByEmpAcctId(empAcctId);
        if (empAcct == null) return;

        List<String> roleIds = empLoginMapper.selectRoleIdsByEmpAcctId(empAcctId);
        empAcct.setRoleIds(roleIds);

        Set<String> authCodes = new HashSet<>();
        // 필요하면 실제 조회로 채우기:
        // authCodes.addAll(empLoginMapper.selectAuthCodesByEmpAcctId(empAcctId));
        empAcct.setAuthCodes(authCodes);

        session.setAttribute(SessionConst.LOGIN_EMP, loginSessionBuilder.build(empAcct));
    }
}
