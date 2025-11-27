package store.yd2team.common.service;

import java.sql.Date;

import lombok.Data;

@Data
public class VendVO {
	
	private String vendId;// 업체ID
	private String vendNm;// 업체명
	private String rpstrNm;// 대표자명
	private Long bizno;// 사업자등록번호
	private Long hp;// 휴대폰번호
	private Long tel;// 전화번호
	private String email;// 이메일
	private String addr;// 주소
	private String bizcnd;// 업태업종
	private Date joinDt;// 가입일자
	private String st;// 상태
	private Date creaDt;// 생성일자
	private String creaBy;// 생성자
	private Date updtDt;// 수정일자
	private String updtBy;// 수정자
	
}// end class