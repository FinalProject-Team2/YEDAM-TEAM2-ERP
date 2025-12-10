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
public class EmpVO {

	private String empId;        // 주식별자
	private String deptId;       // 부서ID FK
	private String deptHeadId;       // 사원 테이블 FK (부서장)
	private String uprDeptId;       // 상위부서ID
	private String deptNm;       // 부서이름
	private String basiNo;       // 부서별 근무시간기준 FK	
	private String vendId;       // 구독시 생성된ID FK
	private String nm;           // 개인정보-이름
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date brthdy;         // 개인정보-생년월일
	private String cttpc;        // 개인정보-연락처
	private String email;        // 개인정보-이메일
	private String dty;          // ex HRM(인적자원관리)
	private String clsf;         // 사원/대리/과장/부장/이사
	private String rspofc;       // 팀원/팀장/관리자/임원
	private String emplymTy;     // 정직원/계약직/일용직
	private Long bslry;          // 급여기준방식1
	private Long pymhr;          // 급여기준방식2
	private String salaryType;   // 급여타입
	private Long salaryInput;   // 급여타입의금액
	private String acnutno;      // 급여받을 계좌
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date encpn;          // 입사날짜
	private String hffcSt;       // 재직/퇴직/휴직/퇴사
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date creaDt;         // 생성날짜
	private String creaBy;       // 생성자
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updtDt;         // 수정날짜
	private String updtBy;       // 수정자
	private String proofPhoto;   // 사진위치
	
	//join에 필요한 컬럼
	private String clsfNm;   // 직급
	private String rspofcNm;   // 급여타입
	private String emplymTyNm;   // 급여타입
	private String hffcStNm;   // 급여타입
	private String basiNm;       // 부서별 근무시간기준 이름
}
