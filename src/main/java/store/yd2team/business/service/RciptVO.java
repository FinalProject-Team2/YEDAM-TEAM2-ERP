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

    // 거래일자
    // SELECT r.trns_dt
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trnsDt;

    // 거래내역(내용)
    // SELECT r.sale_ctnt
    private String saleCtnt;

    // 매출금액
    // SELECT r.sale_amt
    private Long saleAmt;

    // 차입금액(차변금액 등 의미에 맞게 사용)
    // SELECT r.borw_amt
    private Long borwAmt;

    // 원금잔액
    // SELECT r.prinsum_baln
    private Long prinsumBaln;

    // 수금예정금액
    // SELECT r.rcipt_appo_amt AS rciptAppoAmt
    private Long rciptAppoAmt;

    // 수금예정일
    // SELECT r.rcipt_appo_dt AS rciptAppoDt
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate rciptAppoDt;

    // 최종수금일
    // SELECT r.ltst_rcipt_dt AS ltstRciptDt
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate ltstRciptDt;
    
    // -- 입금내역 테이블(tb_rcipt_detail)
    private String vendId;
    private String rciptDt;
    private Long rciptAmt;
    private String pmtMtd;
    private String rm;
}
