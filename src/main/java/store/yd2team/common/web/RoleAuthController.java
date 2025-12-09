package store.yd2team.common.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.MenuAuthVO;
import store.yd2team.common.service.RoleAuthService;
import store.yd2team.common.service.RoleVO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authModal")
public class RoleAuthController {
	
	private final RoleAuthService roleAuthService;

    private String getVendId(HttpSession session) {
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        return (loginEmp != null ? loginEmp.getVendId() : null);
    }

    /**
     * 역할 명 자동완성
     * GET /api/auth/roles/nameSuggest?keyword=...
     */
    @GetMapping("/roles/nameSuggest")
    public List<RoleVO> getRoleNameSuggest(@RequestParam("keyword") String keyword,
                                           HttpSession session) {

        String vendId = getVendId(session);
        return roleAuthService.getRoleNameSuggest(vendId, keyword);
    }

    /**
     * 역할 목록 조회
     * GET /api/auth/roles?roleNm=&roleTy=&useYn=
     *  - 모달/전체 페이지 둘 다 여기 사용
     */
    @GetMapping("/roles")
    public List<RoleVO> getRoleList(@RequestParam(value = "roleNm", required = false) String roleNm,
                                    @RequestParam(value = "roleTy", required = false) String roleTy,
                                    @RequestParam(value = "useYn",  required = false) String useYn,
                                    HttpSession session) {

        String vendId = getVendId(session);
        return roleAuthService.getRoleList(vendId, roleNm, roleTy, useYn);
    }

    /**
     * 권한(메뉴) 목록 조회
     * GET /api/auth/menus?moduleId=d1&roleId=...
     */
    @GetMapping("/menus")
    public List<MenuAuthVO> getMenuAuthList(@RequestParam("moduleId") String moduleId,
                                            @RequestParam(value = "roleId", required = false) String roleId,
                                            HttpSession session) {

        String vendId = getVendId(session);
        return roleAuthService.getMenuAuthList(vendId, roleId, moduleId);
    }

}
