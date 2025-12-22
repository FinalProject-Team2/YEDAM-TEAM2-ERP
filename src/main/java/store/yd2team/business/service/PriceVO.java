package store.yd2team.business.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PriceVO {

	// 조회 결과
	private String priceId;
    private String priceName;
    private String priceType;
    private Double percent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate beginDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;
    private String yn;
    private String rm;
    
    private String unit;
    
    // session
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creaDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updtDt;
    private String vendId;
    private String creaBy;
    private String updtBy;
    
    private String type;
    
 // 다건 디테일 목록 (VO 분리 안 할 때)
    private List<Map<String, Object>> detailList;
    
    
    // 조회조건
    private String searchPriceName; 
    // 유형 검색 (거래처별 / 품목별)
    private String searchType;
    // 적용일 검색(특정 날짜 1개로 BETWEEN 조건)
    private String searchApplcDt;
    
    // 고객사모달 저장된 고객사 선택
    private String custcomId;
    private String custcomName;
    
    // 최대 할인율
    private Integer dcRate;
    private String pricePolicyId;
    private String policyType;
    private String productId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate applcDt;
}
