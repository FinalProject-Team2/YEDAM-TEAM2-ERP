package store.yd2team.common.service;

import java.util.Date;

import lombok.Data;

@Data
public class OprtrAcctVO {
	
	private String oprtrAcctId;
	private String loginId;
	private String loginPwd;	
	private Date creaDt;
	private String creaBy;
	private Date updtDt;
	private String updtBy;

}
