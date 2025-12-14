package store.yd2team.common.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MenuAuthDto {
	
	// 어떤 역할의 권한인지
	private String roleId;
	
	// tb_menu 쪽
    private String menuId;
    private String vendId;      // NULL일 수도 있음
    private String menuNm;
    private String menuUrl;
    private String moduleId;    // d1 / d2 / d3
    private String prntMenuId;
    private Long   sortOrd;

    // tb_auth 쪽 (권한 플래그, 없으면 N 기본)
    private String selYn;       // 조회
    private String insYn;       // 등록
    private String updtYn;      // 수정
    private String delYn;       // 삭제

    private Integer canRead;   // 1 or 0
    private Integer canWrite;  // 1 or 0
    private Integer canDelete; // 1 or 0
    
    private List<MenuAuthDto> children = new ArrayList<>();

    // 편하게 쓰려고 boolean 헬퍼도 하나 넣어두면 좋음
    public boolean isReadable() { return canRead != null && canRead == 1; }
    public boolean isWritable() { return canWrite != null && canWrite == 1; }
    public boolean isDeletable() { return canDelete != null && canDelete == 1; }
    
    public boolean isFolder() { return menuUrl == null || menuUrl.isBlank(); }
}
