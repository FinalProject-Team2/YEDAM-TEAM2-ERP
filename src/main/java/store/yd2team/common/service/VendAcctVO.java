package store.yd2team.common.service;

import java.sql.Date;

import lombok.Data;

@Data
public class VendAcctVO {
	
	private String vendAcctId;// 업체계정ID
	private String vendId;// 업체ID
	private String loginId;// 로그인ID
	private String loginPwd;// 로그인비밀번호
	private String st;// 상태
	private String yn;// 사용여부
	private Long failrCnt;// 실패횟수
	private Date lockDttm;// 잠금일시
	private Date lastLogin;// 최종로그인
	private Date creaDt;// 생성일자
	private String creaBy;// 생성자
	private Date updtDt;// 수정일자
	private String updtBy;// 수정자
	
}// end class