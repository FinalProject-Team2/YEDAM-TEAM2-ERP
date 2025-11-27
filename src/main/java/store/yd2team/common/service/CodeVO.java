package store.yd2team.common.service;

import java.util.Date;

import lombok.Data;

@Data
public class CodeVO {
	
	private String codeId;	// 공통 코드 PK
	private String vendId;	// 회사 코드
	private String codeNm; // 코드 이름
	private char yn;	// 코드 사용 유무
	
	private String grpId; // 공통 코드 그룹 PK
	private String grpNm; // 코드 그룹 이름
	private String dc; // 코드 그룹 설명
	private char oprtrYn; // 운영자 사용 유무
	
	private Date creaDt; // 생성일시
	private String creaBy; // 생성자
	private Date updtDt; // 수정일시
	private String updtBy; // 수정자
	
}
