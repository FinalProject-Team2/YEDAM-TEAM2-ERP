package store.yd2team.business.service;

import lombok.Data;

/**
 * 채권 - 미수/연체관리 VO
 * (tb_atmpt / tb_atmpt_detail / tb_dely_mvg_hist + 화면 조회용 컬럼)
 */
@Data
public class AtmptVO {

    /* ==========================
     * tb_atmpt (미수헤더)
     * ========================== */
    private String atmptNo;          // ATMPT_NO      미수번호(PK)
    private String cdtlnNo;          // CDTLN_NO      여신한도번호(FK)
    private String vendId;           // VEND_ID       사업장/거래처ID
    private String custcomId;        // CUSTCOM_ID    고객코드
    private Long basisAtmptBlce;     // BASIS_ATMPT_BLCE  기초미수잔액
    private Long atmptBlce;          // ATMPT_BLCE        현재미수잔액
    private String creaBy;

    /* ==========================
     * tb_atmpt_detail (미수상세)
     * ========================== */
    private String atmptDetailId;    // ATMPT_DETAIL_ID   미수상세ID(PK)
    // VEND_ID, CUSTCOM_ID 는 상단 필드 재사용
    private Long receiptAdsmAmt;     // RECEIPT_ADSM_AMT  수금누계금액
    private Long atmptAmt;           // ATMPT_AMT         미수금액(상세)
    private String recptFg;          // RECPT_FG          수금구분(현금/현물/계좌/카드/어음 등)
    private String recptExpcDt;      // RECPT_EXPC_DT     수금예정일(yyyyMMdd)
    private String arrrgFg;          // ARRRG_FG          연체구분
    private String rpayAt;           // RPAY_AT           상환여부(Y/N)

    /* ==========================
     * tb_dely_mvg_hist (연체이력, 필요 시 사용)
     * ========================== */
    private String histNo;           // HIST_NO
    private String customId;         // CUSTOM_ID        (고객ID, 필요하면 custcomId 로 매핑)
    private String atmtpNo;          // ATMTP_NO         (ATMPT_NO 와 FK)
    private String contDt;           // CONT_DT          처리일자
    private String mvgRslt;          // MVG_RSLT         조치결과
    private String coctFg;           // COCT_FG          조치구분
    private String cntn;             // CNTN             비고/내용

    /* ==========================
     * 조인/표시용 컬럼 (그리드, 상세표시용)
     * ========================== */
    private String custcomName;      // 고객사명   (tb_custcom 조인)
    private String creditGrad;       // 신용등급   (여신테이블 조인)

    private Long thisMonthSaleAmt;   // 당월매출금액 (매출테이블 집계)
    private Long rciptAmt;           // 수금금액     (상세 또는 입금집계)
    private Long remainAtmptAmt;     // 미수금액(표시용) = atmptBlce 등

    /* ==========================
     * 검색조건 영역
     * ========================== */
    private String searchCustcomId;      // 검색-고객코드
    private String searchCustcomName;    // 검색-고객사
    private String searchCreditGrad;     // 검색-신용등급
    private String searchArrrgFg;        // 검색-연체구분
}
