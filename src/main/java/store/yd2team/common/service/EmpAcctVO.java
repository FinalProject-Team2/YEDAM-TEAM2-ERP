package store.yd2team.common.service;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class EmpAcctVO {
	
	private String empAcctId; // 그냥 pk
	private String vendId; // 회사 코드
	private String empId; // 사원 번호
	
	private String loginId; // 로그인 ID
	private String loginPwd; // 로그인 비밀번호
	
	private String st; // 상태값
	private Integer failCnt; // 로그인 실패 횟수
	private LocalDateTime lockDttm; // 잠금 일시
	private Date lastLogin; // 마지막 로그인 일시
	
	private String yn; // 사용 여부
	private String tempYn; // 임시 비밀번호 여부
	
	private Date creaDt;
	private String creaBy;
	private Date updtDt;
	private String updtBy;
	
	// Session
	private String empNm;   // 사원 이름
	private String deptId;  // 부서 ID
	private String deptNm;  // 부서명
}
