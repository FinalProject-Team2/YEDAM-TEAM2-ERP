package store.yd2team.business.service;

import lombok.Data;

@Data
public class AtmptVO {
	// ========== 검색조건 ==========
    /** 고객코드 */
    private String custcomId;

    /** 검색기간(시작일) - RECPT_EXPC_DT 기준 */
    private String fromDt;

    /** 검색기간(종료일) - RECPT_EXPC_DT 기준 */
    private String toDt;


    // ========== 조회 결과(그리드 컬럼) ==========

    /** 채권/미수 번호 (tb_atmpt.ATMPT_NO) */
    private String atmptNo;

    /** 고객사명 (tb_custcom.CUSTCOM_NAME) */
    private String custcomName;

    /** 미수잔액 (tb_atmpt.ATMPT_BLCE) */
    private Long remainAmt;

    /** 가장 최근 입금일 (MAX(tb_atmpt_detail.RPAY_AT)) */
    private String lastPayDt;

    /** 입금 예정일 (MIN(tb_atmpt_detail.RECPT_EXPC_DT)) */
    private String expectDt;

    /** 총 입금액 (SUM(tb_atmpt_detail.ATMPT_AMT)) */
    private Long paidAmt;
}
