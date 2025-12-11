package store.yd2team.common.dto;

import lombok.Data;

@Data
public class LockAccountSearchDto {

    private String clientName;   // 거래처 명
    private String accountId;    // 계정 ID
    private String userName;     // 사용자 이름
    private String status;       // 상태 코드(r1~r4)
    private String lockStartDt;  // 잠금 시작일 (yyyy-MM-dd 문자열)
    private String lockEndDt;    // 잠금 종료일 (yyyy-MM-dd 문자열)
}
