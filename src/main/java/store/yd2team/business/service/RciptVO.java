package store.yd2team.business.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RciptVO {

//조회 조건 (검색용 필드)
    private String custcomId;  // 고객코드
    private String custcomName;  // 고객사명
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDt;  // 거래일자 시작일 (datepicker: startDt)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;  // 거래일자 종료일 (datepicker: endDt)

    
// 조회 결과 (리스트/그리드용 필드)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trnsDt;  // 거래일자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate ltstRciptDt;  // 최근납입일
    private Long bondAmt;  // 채권금액
    private Long bondBaln;  // 채권잔액
    private String delayFg; // 연체구분
    private String rpayYn; // 상환여부
    
    private Long rciptAmtSum; // 누적입금액
    
    // private String resultMsg; // 프로시저 결괏값 받기용 OUT파라미터 사용 안하는걸로 수정해서 주석처리
    private String rciptId; //입금할때 필요한 채권PK
    
// 입금내역 테이블(tb_rcipt_detail)
    private String rciptDt;  // 입금일자 (저장/프로시저 전달용)
    private Long rciptAmt;  // 입금급액
    private String pmtMtd;    // 결제방법
    private String rm;  // 비고
    
    
 // 공통 세션정보
    private String empId;
    private String vendId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creaDt;
    private String creaBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updtDt;
    private String updtBy;
}
