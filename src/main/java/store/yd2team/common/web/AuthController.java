package store.yd2team.common.web;

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
import store.yd2team.common.dto.AuthSaveResult;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.RoleSaveRequest;
import store.yd2team.common.dto.RoleSaveResult;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.AuthManageService;
import store.yd2team.common.service.RoleVO;
import store.yd2team.common.util.LoginSessionRefresher;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth") 
public class AuthController {
	
	private final AuthManageService authManageService;
	
	private final LoginSessionRefresher loginSessionRefresher;

    /** 세션에서 로그인 사용자(회사 ID 포함) 가져오기 */
    private SessionDto getLoginEmp(HttpSession session) {
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        if (loginEmp == null) {
            // 필요하면 커스텀 예외로 바꿔도 됨
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }
        return loginEmp;
    }

    @GetMapping("/roles")
    public List<RoleVO> getRoleList(@RequestParam(name = "roleNm", required = false) String roleNm,
                                    @RequestParam(name = "roleTy", required = false) String roleTy,
                                    @RequestParam(name = "useYn",  required = false) String useYn,
                                    HttpSession session) {

        SessionDto login = getLoginEmp(session);
        String vendId = login.getVendId();  // 

        log.debug("GET /api/auth/roles roleNm={}, roleTy={}, useYn={}", roleNm, roleTy, useYn);

        return authManageService.getRoleList(vendId, roleNm, roleTy, useYn);
    }

    @GetMapping("/menus")
    public List<MenuAuthDto> getMenuAuth(@RequestParam("roleId")   String roleId,
                                         @RequestParam("moduleId") String moduleId,
                                         HttpSession session) {

        SessionDto login = getLoginEmp(session);
        String vendId = login.getVendId();

        log.debug("GET /api/auth/menus roleId={}, moduleId={}", roleId, moduleId);

        return authManageService.getMenuAuthByRoleAndModule(vendId, roleId, moduleId);
    }
    
    @PostMapping("/menus/save")
    public java.util.Map<String, Object> saveMenuAuth(@RequestBody List<MenuAuthDto> authList,
                                                      HttpSession session) {

        SessionDto login = getLoginEmp(session);
        String vendId = login.getVendId();
        String updtBy = login.getEmpId();

        if (authList == null || authList.isEmpty()) {
            throw new IllegalArgumentException("저장할 권한 데이터가 없습니다.");
        }

        String roleId = authList.get(0).getRoleId();
        if (roleId == null || roleId.isBlank()) {
            throw new IllegalArgumentException("roleId가 없습니다.");
        }

        log.debug("POST /api/auth/menus/save vendId={}, roleId={}, size={}",
                  vendId, roleId, authList.size());

        AuthSaveResult result = authManageService.saveMenuAuth(vendId, roleId, authList, updtBy);
        
        loginSessionRefresher.refresh(session, login.getEmpAcctId());

        java.util.Map<String, Object> res = new java.util.HashMap<>();
        res.put("success", true);
        res.put("mode", result.isFirstSave() ? "insert" : "update");
        res.put("count", result.getAffectedCount());
        return res;
    }
    
    @PostMapping("/roles")
    public java.util.Map<String, Object> saveRoleList(@RequestBody RoleSaveRequest req,
                                                      HttpSession session) {

        SessionDto login = getLoginEmp(session);
        String vendId = login.getVendId();   
        String empId  = login.getEmpId();    

        RoleSaveResult result = authManageService.saveRoleList(vendId, empId, req);

        java.util.Map<String, Object> res = new java.util.HashMap<>();
        res.put("success", true);
        res.put("createdCount", result.getCreatedCount());
        res.put("updatedCount", result.getUpdatedCount());
        return res;
    }
    
    @GetMapping("/roles/nameSuggest")
    public List<RoleVO> suggestRoleNames(
            @RequestParam("keyword") String keyword,
            HttpSession session
    ) {
        SessionDto login = getLoginEmp(session);
        String vendId = login.getVendId();

        log.debug("GET /api/auth/roles/name-suggest vendId={}, keyword={}", vendId, keyword);

        return authManageService.getRoleNameSuggestList(vendId, keyword);
    }
}
