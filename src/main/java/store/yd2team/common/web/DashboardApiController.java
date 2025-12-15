package store.yd2team.common.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.DashboardMapper;
import store.yd2team.common.util.LoginSession;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final DashboardMapper dashboardMapper;

    private String vendId() {
        SessionDto s = LoginSession.getLoginSession();
        return (s == null) ? null : s.getVendId();
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        String vendId = vendId();

        Map<String, Object> res = new HashMap<>();
        res.put("summary", dashboardMapper.selectSummary(vendId));
        res.put("acctStatusDist", dashboardMapper.selectAcctStatusDist(vendId));
        res.put("roleTypeCount", dashboardMapper.selectRoleTypeCount(vendId));
        return res;
    }

    @GetMapping("/recent")
    public Map<String, Object> recent() {
        String vendId = vendId();

        Map<String, Object> res = new HashMap<>();
        res.put("topLoginFail", dashboardMapper.selectTopLoginFail(vendId));
        res.put("recentAuthChanges", dashboardMapper.selectRecentAuthChanges(vendId));
        return res;
    }
}
