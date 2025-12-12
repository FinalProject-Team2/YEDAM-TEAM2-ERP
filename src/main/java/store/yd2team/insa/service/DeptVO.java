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
public class DeptVO {

	private int id;      // 주식별자
	private String deptId;      // 주식별자
    private String empId;       // 사원 테이블 FK (부서장)
    private String deptHeadId;       // 사원 테이블 FK (부서장)
    private String vendId;       // 거래처ID
    private String uprDeptId;   // 상위부서 ID(부서_ID 자기참조)
    private String deptNm;      // 부서명
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date creaDt;        // 생성날짜
    private String creaBy;      // 생성자
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updtDt;        // 수정날짜
    private String updtBy;      // 수정자
    
    //조인용 컬럼
    private String uprDeptNm;      // 상위부서명
    private String nm;      // 사원이름

}
