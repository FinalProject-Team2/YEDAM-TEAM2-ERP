package store.yd2team.insa.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EdcVO {

	private String edcId;       // 주식별자
    private String vendId;      // 구독시 생성된ID FK
    private String edcTy;       // 법정의무교육/직무교육/소양교육/사내교육
    private String edcDt;       // 년도검색용
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date edcBeginDt;    // 법정교육 시작날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date edcEndDt;      // 법정교육 종료날짜
    private String edcNm;       // 산업안전보건교육
    
    //tb_edc_trgter 테이블쪽 컬럼
    private String edcTrgterId;       // 교육대상자고유id
    private String empId;       // 사원id
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date complDt;       // 수료받은날
    private String complSt;       // 수료상태
    private String complFile;       // 수료증파일
    
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;        // 생성날짜
    private String creaBy;      // 생성자
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;        // 수정날짜
    private String updtBy;      // 수정자
    
    //join용 필요한 컬럼
    private String nm;      // 사원이름
    private String deptNm;      // 부서이름
    private String edcCnt;      // 교육 대상자 총인원
    private String compl;      // 수료율
    private String edcSel;      // 교육 선정자
    private String rm;      // 비고
}
