package store.yd2team.common.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.EmpDeptDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.CodeService;
import store.yd2team.common.service.CodeVO;
import store.yd2team.common.service.EmpAcctService;

@Controller
@RequiredArgsConstructor
public class CommonPageController {
	
	private final CodeService codeService;
	private final EmpAcctService empAcctService;
	
	// 공통 코드 관리
	@GetMapping("/code")
	public String commonCode() {
		return "common/commonCode";
	}
	
	// 사원 계정 관리
	@GetMapping("/empAcct")
    public String empAcctPage(HttpSession session, Model model) {

        // 세션에서 vendId 가져오기
        SessionDto loginEmp = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        String vendId = (loginEmp != null) ? loginEmp.getVendId() : null;

        // ① 직급 코드 (grp_id = 'k')
        CodeVO jobParam = new CodeVO();
        jobParam.setGrpId("k");
        jobParam.setVendId(vendId);
        List<CodeVO> jobCodeList = codeService.findCode(jobParam);

        // ② 계정 상태 코드 (grp_id = 'r') – 계정 상태도 공통코드에서 쓰고 싶으면
        CodeVO acctParam = new CodeVO();
        acctParam.setGrpId("r");
        acctParam.setVendId(vendId);
        List<CodeVO> acctAllList = codeService.findCode(acctParam);
        
        List<CodeVO> acctStatusList = acctAllList.stream()
                .filter(c -> !"r4".equals(c.getCodeId()))     // TERMINATED 숨김
                .peek(c -> {
                    switch (c.getCodeId()) {
                        case "r1": c.setCodeNm("사용");   break;   // ACTIVE
                        case "r2": c.setCodeNm("잠금");   break;   // LOCKED
                        case "r3": c.setCodeNm("비활성"); break;   // INACTIVE
                        default: /* 그대로 */ break;
                    }
                })
                .toList();
        
        List<EmpDeptDto> deptList = empAcctService.findEmpDeptList(vendId);


        model.addAttribute("jobCodeList", jobCodeList);       // 직급
        model.addAttribute("acctStatusList", acctStatusList); // 계정 상태
        model.addAttribute("deptList", deptList); // 부서

        return "common/empAcct";
    }
	
	// 권한 관리
	@GetMapping("/auth")
	public String auth() {
		return "common/auth";
	}
	
	// 로그인 정책
	@GetMapping("/loginPolicy")
	public String loginPolicy() {
		return "common/loginPolicy";
	}
	
	// 시스템 로그
	@GetMapping("/sysLog")
	public String sysLog() {
		return "common/sysLog";
	}
	
	// 잠금 계정 해제
	@GetMapping("/unlockAcct")
	public String unlockAcct() {
		return "common/unlockAcct";
	}
	
	// 로그인 페이지
	@GetMapping("/logIn")
	public String login() {
		return "common/logIn";
	}
	
}
