package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HldyVO {

    /** 휴일번호 (PK, NUMBER(10)) */
    private Integer hldyNo;

    /** 휴일명 */
    private String hldyNm;

    /**
     * 휴일 월일 (MM-DD)
     * - 화면/JSON: "MM-DD" 형태 (예: "01-01", "12-25")
     * - DB: DATE(2000-MM-DD 로 저장)
     */
    private String hldyMmdd;

    /** 사용여부 코드 (공통코드 tb_code.code_id : e1/e2) */
    private String ynCode;

    /** 사용여부 코드명 (공통코드 tb_code.code_nm : Y/N 등) */
    private String ynNm;

    /** 생성일시 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;

    /** 생성자 */
    private String creaBy;

    /** 수정일시 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;

    /** 수정자 */
    private String updtBy;

    /** 거래처 ID(구독 생성된 ID FK / 회사코드) */
    private String vendId;
}
