package store.yd2team.common.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.dto.SecPolicyResponseDto;
import store.yd2team.common.service.SecPolicyService;
import store.yd2team.common.service.SecPolicyVO;
import store.yd2team.common.util.LoginSession;

@RequiredArgsConstructor
@RestController
public class SecPolicyController {
	
    private final SecPolicyService secPolicyService;

    // 조회
    @GetMapping("/loginPolicy/secPolicy")
    public SecPolicyResponseDto getSecPolicy() {

        String vendId = LoginSession.getVendId();   // 회사 코드

        SecPolicyVO vo = secPolicyService.getByVendIdOrDefault(vendId);
        return SecPolicyResponseDto.ok(vo);
    }
    
    // 저장 , 수정
    @PostMapping("/loginPolicy/secPolicy")
    public SecPolicyResponseDto saveSecPolicy(@RequestBody SecPolicyVO reqVo) {

        String vendId  = LoginSession.getVendId();
        String empId  = LoginSession.getEmpId();

        boolean exists = secPolicyService.existsForVend(vendId);

        try {
            SecPolicyVO saved = secPolicyService.saveForVend(vendId, empId, reqVo);

            String msg = exists
                    ? "보안 정책이 수정되었습니다."
                    : "보안 정책이 저장되었습니다.";

            return SecPolicyResponseDto.ok(msg, saved);

        } catch (IllegalArgumentException e) {
            return SecPolicyResponseDto.fail(e.getMessage());
        }
    }
}
