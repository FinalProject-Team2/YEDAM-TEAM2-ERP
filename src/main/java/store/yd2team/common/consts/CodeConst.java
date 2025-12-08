package store.yd2team.common.consts;

public final class CodeConst {
	
	// === Y/N 공통 (grp_id = 'e') ===
    public static final class Yn {
        public static final String GRP_ID = "e";   // tb_code.grp_id
        public static final String Y      = "e1";  // YES
        public static final String N      = "e2";  // NO

        private Yn() {}
    }

    // === 계정 상태 (grp_id = 'r') ===
    public static final class EmpAcctStatus {
        public static final String GRP_ID     = "r";
        public static final String ACTIVE     = "r1"; // 사용
        public static final String LOCKED     = "r2"; // 잠금
        public static final String INACTIVE   = "r3"; // 비활성
        public static final String TERMINATED = "r4"; // 해지

        private EmpAcctStatus() {}
    }
    
    // === 세션 타임아웃 동작 (grp_id = 't') ===
    public static final class TimeoutAction {
        public static final String GRP_ID        = "t";  // tb_code.grp_id
        public static final String AUTO_LOGOUT   = "t1"; // 자동 로그아웃
        public static final String WARN_LOGOUT   = "t2"; // 경고 후 로그아웃

        private TimeoutAction() {}
    }
    
    // === 모듈 유형 구분 (grp_id = 'd' ) ===
    public static final class ModuleType {
    	public static final String GRP_ID        = "d";  // 모듈 유형
    	public static final String INSA          = "d1"; // 인사
    	public static final String COMMON        = "d2"; // 공통
    	public static final String BUSI          = "d3"; // 영업
    }
    
    // 인스턴스 생성 방지
    private CodeConst() {} 
	
}
