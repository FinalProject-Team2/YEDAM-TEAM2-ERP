package store.yd2team.insa.service;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MlssVO {

	
    private String mlssId; // 주식별자    
    private String mlssEmpId; // 주식별자    
    private String empId; // 평가당하는 사원 FK
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date evlBeginDt; // 다면평가 시작날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date evlEndDt; // 다면평가 종료날짜    
    private String mlssNm; // 다면평가 이름
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt; // 생성날짜    
    private String creaBy; // 생성자
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt; // 수정날짜    
    private String updtBy; // 수정자    
    private Double suprr; // 평가완료카운트 기록 (상사)    
    private Double crk; // 평가완료카운트 기록 (동료/하위)    
    private Double self; // 평가완료카운트 기록 (자기평가)
    
    //mlss_iem 에만 있는 컬럼
    private String mlssIemId; //mlssIemId 
    private String ability; //다면평가 그룹 
    private String mlssIem; //다면평가 세부항목 
    
    //mlss_wrter 에만 있는 컬럼
    private String mlssWrterId;
    private Double score;
    private String evaleRelate;
    
    //비즈니스 로직에 필요한 컬럼
    private String vendId;       // 구독시 생성된ID FK
    private String nm;       // 사원이름
    private String clsf;       // 사원직급
    private int mlssEmpIdCount;       // 완료여부카운트
    
    


}
