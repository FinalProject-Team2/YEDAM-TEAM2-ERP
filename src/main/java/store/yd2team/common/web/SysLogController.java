package store.yd2team.common.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.dto.SysLogSearchCond;
import store.yd2team.common.service.SysLogVO;
import store.yd2team.common.service.SystemLogService;

@RestController
@RequestMapping("/api/sysLog")
@RequiredArgsConstructor
public class SysLogController {

    private final SystemLogService systemLogService;

    @GetMapping("/list")
    public Map<String, Object> getLogList(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {

        SysLogSearchCond cond = new SysLogSearchCond();
        cond.setAccountId(accountId);
        cond.setModule(module);
        cond.setAction(action);
        cond.setStartDate(startDate);
        cond.setEndDate(endDate);

        List<SysLogVO> list = systemLogService.getLogList(cond);

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        Map<String, Object> data = new HashMap<>();
        data.put("contents", list);
        data.put("pagination", Map.of("totalCount", list.size()));

        result.put("data", data);
        return result;
    }
}
