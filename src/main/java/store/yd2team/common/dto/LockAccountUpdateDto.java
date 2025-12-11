package store.yd2team.common.dto;

import lombok.Data;

@Data
public class LockAccountUpdateDto {

    private String empAcctId; // 어떤 계정인지
    private String status;    // 바꿀 상태 코드 (r1~r4)
    private String updtBy;    // 수정자(loginId 등)
}
