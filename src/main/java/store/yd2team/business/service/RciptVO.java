package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RciptVO {

    /* =========================
     *  조회 조건 (검색용 필드)
     * ========================= */

    // 고객코드
    private String custcomId;

    // 고객사명
    private String custcomName;

    // 담당자
    private String psch;

    // 거래일자 시작일 (datepicker: startDt)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDt;

    // 거래일자 종료일 (datepicker: endDt)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;


    /* =========================
     *  조회 결과 (리스트/그리드용 필드)
     * ========================= */

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    // 거래일자
    private LocalDate trnsDt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    
    // 최근납입일
    private LocalDate ltstRciptDt;

    // 채권금액
    private Long bondAmt;
    
    // 매출금액
    private Long saleAmt;

    // 채권잔액
    private Long bondBaln;

	/*
	 * // 입금약속액 private Long rciptAppoAmt;
	 * 
	 * // 입금약속일
	 * 
	 * @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") private
	 * LocalDate rciptAppoDt;
	 */


    
    // -- 입금내역 테이블(tb_rcipt_detail)
    private String vendId;
    private String rciptDt;
    private Long rciptAmt;  //입금급액
    private String pmtMtd; //결제구분
    private String rm; //비고
}
