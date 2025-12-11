package store.yd2team.common.dto;

import lombok.Data;

@Data
public class AutoCompleteItemDto {

    private String id;     // 내부 식별자 (vendId, empAcctId 등)
    private String label;  // 목록에 표시할 텍스트
    private String value;  // input에 채워 넣을 값
}
