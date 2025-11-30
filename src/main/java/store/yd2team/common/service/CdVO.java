package store.yd2team.common.service;

import java.sql.Date;

import lombok.Data;

@Data
public class CdVO {

	private String codeId;// 코드ID
	private String grpId;// 그룹ID
	private String vendId;// 업체ID
	private String codeNm;// 코드명
	private String yn;// 사용여부
	private Date creaDt;// 생성일자
	private String creaBy;// 생성자
	private Date updtDt;// 수정일자
	private String updtBy;// 수정자
	
}// end class