package store.yd2team.common.dto;

import java.util.List;

import lombok.Data;

@Data
public class MenuModuleViewDto {
	
	private String moduleId;    // d1 / d2 / d3
    private String moduleNm;    // 공통, 인사, 영업 등 (표시용)
    private List<MenuAuthDto> menus;  // 이 모듈에 속한 메뉴들

}
