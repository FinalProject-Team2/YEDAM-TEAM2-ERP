package store.yd2team.common.service;

import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.ACTIVE;
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.INACTIVE;
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.LOCKED;
import static store.yd2team.common.consts.CodeConst.EmpAcctStatus.TERMINATED;
import static store.yd2team.common.consts.CodeConst.Yn.Y;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class EmpAcctVO {
	
	private String empAcctId; // 그냥 pk
	private String vendId;    // 회사 코드
	private String empId;     // 사원 번호
	
	private String loginId;   // 로그인 ID
	private String loginPwd;  // 로그인 비밀번호
	
	private String st;              // 상태값(r1~r4)
	private Integer failCnt;        // 로그인 실패 횟수
	private LocalDateTime lockDttm; // 잠금 일시
	private Date lastLogin;         // 마지막 로그인 일시
	
	private String yn;      // 사용 여부(e1/e2)
	private String tempYn;  // 임시 비밀번호 여부(e1/e2)
	private String masYn;   // 마스터 계정 여부(e1/e2)
	
	private Date creaDt;
	private String creaBy;
	private Date updtDt;
	private String updtBy;
	
	// Session
	private String empNm;   // 사원 이름
	private String deptId;  // 부서 ID
	private String deptNm;  // 부서명
	private String bizcnd;  // 거래처 업종
	private String addr;    // 거래처 주소
	private String cttpc;   // 연락처
	private String hp;      // 거래처 핸드폰 번호
	private String email;
	private String proofPhoto;
	private String jobNm;
	
	private List<String> roleIds;
    private Set<String> authCodes;

	// ==========================
	// 상태(r그룹) 헬퍼 메서드
	// ==========================

	/** 계정 상태가 ACTIVE(r1)인지 */
	public boolean isActive() {
		return ACTIVE.equals(this.st);
	}

	/** 계정 상태가 LOCKED(r2)인지 */
	public boolean isLocked() {
		return LOCKED.equals(this.st);
	}

	/** 계정 상태가 INACTIVE(r3)인지 */
	public boolean isInactive() {
		return INACTIVE.equals(this.st);
	}

	/** 계정 상태가 TERMINATED(r4)인지 */
	public boolean isTerminated() {
		return TERMINATED.equals(this.st);
	}

	// ==========================
	// Y/N(e그룹) 헬퍼 메서드
	// ==========================

	/** 계정 사용 여부 (yn = Y?) */
	public boolean isUse() {
		return Y.equals(this.yn);
	}

	/** 임시 비밀번호 계정인지 (temp_yn = Y?) */
	public boolean isTempPw() {
		return Y.equals(this.tempYn);
	}

	/** 마스터 계정인지 (mas_yn = Y?) */
	public boolean isMaster() {
		return Y.equals(this.masYn);
	}
	
}
