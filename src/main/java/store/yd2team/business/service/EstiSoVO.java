package store.yd2team.business.service;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EstiSoVO {

	// ====== 검색 조건 ======
    private String custcomName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
    
    private String cdtlnNo;        // cdtln_no
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate soDt;           // so_dt (견적일자 or 주문일자)
    private Long ttSupplyAmt;      // tt_supply_amt (상세 합계)
    private String rm;             // rm

    // 공통
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creaDt;
    private String creaBy;         // 등록자
    private String updtBy;         // 수정자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updtDt;
    private String empId;

    // 상세 리스트
    private List<EstiSoDetailVO> detailList;
    
    // 이력보기 고객사
    private String custcomNameText;
    
    
    private Long currStockQy;   // 재고수량 (첫 상품 기준)
    private Long ttSoQy;        // 총 주문수량 (첫 상품 기준)
    
    // 주문 헤더 (tb_so)
    private String soId;
    private String hp;
    private Long ttSupplyPrice;
    private Long ttSurtaxPrice;
    private Long ttPrice;
    private String progrsSt;
    private String stResn;
    private String accpDt;
    private String shipAddr;
    private String psch;
    private String pschTel;
    
    private String codeId;
    private String codeNm;
    
    private String header;
    
    private List<EstiSoDetailVO> details;
    
    // 입금약속
    private Long rciptAppoAmt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate rciptAppoDt;
    private String appoMtd;
}

