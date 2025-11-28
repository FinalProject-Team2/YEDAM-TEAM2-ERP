package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               
@NoArgsConstructor  
@AllArgsConstructor 
@Builder           
public class CodeRegResponseDto {

	private boolean success; // 성공 여부
	private String message; // 안내 메시지
	private String codeId; // (선택) 생성된 코드 ID
	
	// 성공 응답용
    public static CodeRegResponseDto ok(String codeId) {
        return CodeRegResponseDto.builder()
                .success(true)
                .message(null)
                .codeId(codeId)
                .build();
    }

    // 실패 응답용
    public static CodeRegResponseDto fail(String message) {
        return CodeRegResponseDto.builder()
                .success(false)
                .message(message)
                .codeId(null)
                .build();
    }

}
