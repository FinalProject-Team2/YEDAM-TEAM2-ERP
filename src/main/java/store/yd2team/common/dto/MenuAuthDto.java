package store.yd2team.common.dto;

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
}
