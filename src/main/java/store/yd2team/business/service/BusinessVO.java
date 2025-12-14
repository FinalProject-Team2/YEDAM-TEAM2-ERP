package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class BusinessVO {
	    private Long   potentialInfoNo;   	// POTENTIAL_INFO_NO
	    private int   potentialCondNo;   	// POTENTIAL_COND_NO
	    private String vendId;            	// VEND_ID
	    private Integer rank;             	// RANK
	    private String vendNm;            	// 거래처명 VEND_NM
	    private String industryType;      	// INDUSTRY_TYPE
	    private String companySize;       	// 규모 COMPANY_SIZE 
	    private String region;            	// 지역 REGION
	    private String establishDate;     	// 설립일ESTABLISH_DATE (YYYY-MM-DD 형식 문자열)
	    private String startEstablishDate;  // 설립일ESTABLISH_DATE (YYYY-MM-DD 형식 문자열)
	    private String endEstablishDate;     // 설립일ESTABLISH_DATE (YYYY-MM-DD 형식 문자열)
	    private Integer contactCnt;
	
	    private Date creaDt;             // CREA_DT  (문자열로 받기)
	    private String creaBy;           // CREA_BY
	    private Date updtDt;             // UPDT_DT
	    private String updtBy;           // UPDT_BY
	
	    // 밑에 세 개는 지금 테이블에는 없으니까 일단 안 써도 됨
	    private String stdrId;
	    private String stdrIteamInfo;
	    private Integer infoScore;
	    
	    // === AI로 계산한 리드 점수 (DB 컬럼 없이 화면용) ===
	    private Integer leadScore;
}