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
    
    // 인스턴스 생성 방지
    private CodeConst() {} 
	
}
