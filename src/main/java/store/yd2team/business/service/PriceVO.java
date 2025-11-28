package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PriceVO {

	// 조회 결과
	private String priceId;
    private String priceName;
    private String type;
    private Double percent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate beginDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;
    private String yn;
    private String rm;
    private String creaBy;
    private String updtBy;
    
    
    // 조회조건
    private String searchPriceName; 
    // 유형 검색 (거래처별 / 품목별)
    private String searchType;
    // 적용일 검색(특정 날짜 1개로 BETWEEN 조건)
    private String searchApplcDt;
}
