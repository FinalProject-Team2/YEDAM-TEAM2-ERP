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
    		@RequestParam(name="accountId", required=false) String accountId,
            @RequestParam(name="module",   required=false) String module,
            @RequestParam(name="action",   required=false) String action,
            @RequestParam(name="startDate",required=false) String startDate,
            @RequestParam(name="endDate",  required=false) String endDate
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
    
    @GetMapping("/loginIds")
    public Map<String, Object> loginIdAutoComplete(@RequestParam(name="q", required=false) String q) {
        List<String> list = systemLogService.getLoginIdList(q == null ? "" : q);

        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("data", list);
        return result;
    }
    
    @GetMapping("/filters")
    public Map<String, Object> filters() {
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        Map<String, Object> data = new HashMap<>();
        data.put("modules", systemLogService.getCodeList("d"));   // d1 인사, d2 공통, d3 영업
        data.put("actions", systemLogService.getCodeList("ac"));  // ac1 로그인, ac2 저장, ac3 삭제

        result.put("data", data);
        return result;
    }
}
