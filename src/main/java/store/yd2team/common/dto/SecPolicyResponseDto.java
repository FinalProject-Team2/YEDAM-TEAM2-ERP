package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.yd2team.common.service.SecPolicyVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecPolicyResponseDto {

	private boolean success;   // 성공 여부
    private String message;    // 안내 메시지
    private SecPolicyVO policy; // 실제 정책 데이터

    // 성공 응답
    public static SecPolicyResponseDto ok(SecPolicyVO policy) {
        return new SecPolicyResponseDto(true, null, policy);
    }
    
    public static SecPolicyResponseDto ok(String message, SecPolicyVO policy) {
        return new SecPolicyResponseDto(true, message, policy);
    }
    
    // 실패 응답
    public static SecPolicyResponseDto fail(String message) {
        return new SecPolicyResponseDto(false, message, null);
    }
	
}
