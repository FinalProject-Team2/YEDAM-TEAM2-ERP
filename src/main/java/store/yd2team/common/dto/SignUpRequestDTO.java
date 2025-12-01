package store.yd2team.common.dto;

import lombok.Data;

/**
 * 회원가입 폼에서 넘어오는 값들을 담는 DTO.
 */
@Data
public class SignUpRequestDTO {
	// tb_vend_acct 관련 필드
	private String userId;          // 화면 아이디 입력값 → tb_vend_acct.login_id
	private String password;        // 화면 비밀번호 → tb_vend_acct.login_pwd
	
	// tb_vend 관련 필드
	private String bizName;         // 상호명 → tb_vend.vend_nm
	private String ownerName;       // 대표자 이름 → tb_vend.rpstr_nm
	private String bizRegNo;        // 사업자등록번호(문자열) → tb_vend.bizno
	private String mobileNo;        // 휴대폰번호(문자열) → tb_vend.hp
	private String telNo;           // 대표 전화번호(문자열) → tb_vend.tel
	private String email;           // 대표 이메일(완성형) → tb_vend.email
	private String addr;            // 대표 사업장 주소(도로명+상세) → tb_vend.addr
	private String bizType;         // 업태/업종 → tb_vend.bizcnd
	
	// 약관 동의 등 추가 값이 필요하면 여기에 필드 추가
}
