package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class BusinessVO {
	private String stdrDetailId;
 	private int potentialInfoNo;
	private String vendId;
	private String industryTypeCond;
	private String companySizeCond;
	private Date establishDtCond;
	private String regionCond;
	private boolean yn;
	
	private String stdrId;
	private String stdrIteamInfo;
	private String nfoScore;
}
