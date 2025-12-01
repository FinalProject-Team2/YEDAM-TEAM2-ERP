package store.yd2team.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.yd2team.common.service.EmpAcctVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultDto {
	
	private boolean success;
    private String message;
    private EmpAcctVO empAcct;

    public static LoginResultDto ok(EmpAcctVO vo) {
        return new LoginResultDto(true, null, vo);
    }

    public static LoginResultDto fail(String message) {
        return new LoginResultDto(false, message, null);
    }
	
}
