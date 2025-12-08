package store.yd2team.common.dto;

import lombok.Data;

@Data
public class AuthSaveResult {
	private boolean firstSave;    // true = 최초 저장, false = 수정
    private int affectedCount;    // MERGE로 영향받은 행 수 (대략적인 개수)
}
