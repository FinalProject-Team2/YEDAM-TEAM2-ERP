package store.yd2team.business.service;

import lombok.Data;

@Data
public class PriceVO {

	// 조회 결과
	private Long priceId;
    private String priceName;
    private String type;
    private Double percent;
    private String beginDt;
    private String endDt;
    private String yn;
    private String rm;
    
    
    // 조회조건
    private String searchPriceName; 
    // 유형 검색 (거래처별 / 품목별)
    private String searchType;
    // 적용일 검색(특정 날짜 1개로 BETWEEN 조건)
    private String searchApplcDt;
}
