package store.yd2team.insa.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.util.LoginSession;
import store.yd2team.insa.mapper.AttdStatusMapper;
import store.yd2team.insa.service.AttdStatusService;
import store.yd2team.insa.service.AttdStatusVO;

@Service
@RequiredArgsConstructor
public class AttdStatusServiceImpl implements AttdStatusService {

    private final AttdStatusMapper attdStatusMapper;

    @Override
    public List<AttdStatusVO> selectAttdStatusList(AttdStatusVO searchVO) {

        // ===== 세션 =====
        String vendId    = LoginSession.getVendId();
        String empId     = LoginSession.getEmpId();
        String empAcctId = LoginSession.getEmpAcctId();

        searchVO.setVendId(vendId);
        searchVO.setLoginEmpId(empId);

        // 관리자 여부 판단
        int cnt = attdStatusMapper.selectHrAdminCnt(vendId, empAcctId);
        boolean isAdmin = cnt > 0;

        if (isAdmin) {
            return attdStatusMapper.selectAttdListAdmin(searchVO);
        } else {
            return attdStatusMapper.selectAttdListUser(searchVO);
        }
    }

    @Override
    public boolean isHrAdminCurrentUser() {
        String vendId    = LoginSession.getVendId();
        String empAcctId = LoginSession.getEmpAcctId();

        if (vendId == null || empAcctId == null) {
            return false;
        }

        int cnt = attdStatusMapper.selectHrAdminCnt(vendId, empAcctId);
        return cnt > 0;
    }
}
