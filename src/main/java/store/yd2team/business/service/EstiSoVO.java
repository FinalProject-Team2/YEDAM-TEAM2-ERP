package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EstiSoVO {

	// ====== 검색 조건 ======
    private String custcomName;
    private LocalDate dueStart;
    private LocalDate dueEnd;

    // ====== 조회 결과 컬럼 ======
    private String estiId;
    private String version;
    private String custcomNameResult;
    private Long cdtlnLmt;
    private Long totalAmount;
    private String productName;     // 대표 상품명 + "외 n건"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDt;
    private String estiSt;
    private String recallResn;
    
    // ===== 공통코드 부분 =====
    private String estiStNm;  // 공통코드명(대기/승인/반려)
    
    // 견적서 모달 판매단가
    private String productId;
    private Long basisSaleAmt;
    
    //===== 모달 고객사 auto complete
    private String custcomId;     // 고객사ID
    private String vendId;       // 거래처ID
}

