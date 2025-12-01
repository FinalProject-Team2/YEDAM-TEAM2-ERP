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
public class YrycVO {

	private String yrycId;          // 주식별자
    private int vcatnYear;          // 연차계산대상연도
    private int ttOccrrncYryc;      // 연차생성시 기록되는컬럼
    private int useYryc;            // 연차소진시 기록되는컬럼
    private int remndrYryc;         // 연차소진시 기록되는 컬럼
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date yrycExtshDt;       // 회사 별 연차소멸 기준날짜설정 컬럼
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;            // 생성날짜
    private String creaBy;          // 생성자
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;            // 수정날짜
    private String updtBy;          // 수정자
    private String empId;           // 사원 ID FK
    
    //join용 컬럼
    private String vendId;           // 거래처 ID FK

}
