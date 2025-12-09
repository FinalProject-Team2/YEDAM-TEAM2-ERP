package store.yd2team.common.web;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpAcctEmployeeDto;
import store.yd2team.common.dto.EmpAcctRoleDto;
import store.yd2team.common.dto.EmpAcctSaveRequestDto;
import store.yd2team.common.dto.EmpAcctSaveResultDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.EmpAcctService;

@Slf4j
@RestController
@RequestMapping("/api/empAcct")
@RequiredArgsConstructor
public class EmpAcctController {
	
	private final EmpAcctService empAcctService;

    @GetMapping("/employees")
    public List<EmpAcctEmployeeDto> getEmployees(
            @RequestParam(name = "vendId"  , required = false) String vendId,
            @RequestParam(name = "deptName", required = false) String deptName,
            @RequestParam(name = "jobName" , required = false) String jobName,
            @RequestParam(name = "empName" , required = false) String empName,
            @RequestParam(name = "loginId" , required = false) String loginId,
            HttpSession session
    ) {

        // vendId 없으면 세션에서 보완
        if (vendId == null || vendId.isBlank()) {
            SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
            if (loginEmp != null) {
                vendId = loginEmp.getVendId();
            }
        }

        if (vendId == null || vendId.isBlank()) {
            log.warn("[EmpAcctRestController] vendId 가 없습니다. 빈 목록 반환");
            return Collections.emptyList();
        }

        return empAcctService.searchEmployees(vendId,
								              deptName,
								              jobName,
								              empName,
								              loginId);
    }
	
    @GetMapping("/autocomplete/empName")
    public List<EmpAcctEmployeeDto> autocompleteEmpName(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "vendId", required = false) String vendId,
            HttpSession session
    ) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        if (vendId == null || vendId.isBlank()) {
            SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
            if (loginEmp != null) {
                vendId = loginEmp.getVendId();
            }
        }

        if (vendId == null || vendId.isBlank()) {
            log.warn("[EmpAcctController] vendId 없음, autocompleteEmpName 빈 목록 반환");
            return Collections.emptyList();
        }

        return empAcctService.autocompleteEmpName(vendId, keyword);
    }

    @GetMapping("/autocomplete/loginId")
    public List<EmpAcctEmployeeDto> autocompleteLoginId(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "vendId", required = false) String vendId,
            HttpSession session
    ) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        if (vendId == null || vendId.isBlank()) {
            SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
            if (loginEmp != null) {
                vendId = loginEmp.getVendId();
            }
        }

        if (vendId == null || vendId.isBlank()) {
            log.warn("[EmpAcctController] vendId 없음, autocompleteLoginId 빈 목록 반환");
            return Collections.emptyList();
        }

        return empAcctService.autocompleteLoginId(vendId, keyword);
    }
    
    @PostMapping("/save")
    public EmpAcctSaveResultDto saveEmpAccount(@RequestBody EmpAcctSaveRequestDto req,
                                               HttpSession session) {

        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        String loginEmpId = (loginEmp != null ? loginEmp.getEmpId() : null);
        
        log.info(">>> [EmpAcctController] SAVE called. empAcctId={}, loginId={}, acctStatus={}, roleIdsSize={}",
                req.getEmpAcctId(),
                req.getLoginId(),
                req.getAcctStatus(),
                (req.getRoleIds() == null ? "null" : req.getRoleIds().size()));

        return empAcctService.saveEmpAccount(req, loginEmpId);
    }
    
    @GetMapping("/roles")
    public List<EmpAcctRoleDto> getEmpAcctRoles(
            @RequestParam("empAcctId") String empAcctId) {

        return empAcctService.getEmpAcctRoles(empAcctId);
    }
}
