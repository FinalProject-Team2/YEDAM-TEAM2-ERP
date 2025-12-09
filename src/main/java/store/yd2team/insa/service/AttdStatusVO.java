package store.yd2team.insa.service;

import lombok.Data;

/**
 * 근태현황 조회 VO
 */
@Data
public class AttdStatusVO {

    // ====== 조회 결과 ======
    private String wkDt;       // 근무일자 (YYYY.MM.DD)
    private String deptNm;     // 부서명
    private String roleNm;     // 직급(직책)
    private String empId;      // 사원ID
    private String empNm;      // 사원명
    private String attdTy;     // 근태유형(지각/조퇴/결근/휴가 등)
    private String attdIn;     // 출근시간(HH24:MI)
    private String attdOut;    // 퇴근시간(HH24:MI)

    // ====== 검색 조건 ======
    private String deptId;     // 부서ID
    private String empNmCond;  // 사원명(검색어)
    private String startDt;    // 근무 시작일 (YYYY-MM-DD)
    private String endDt;      // 근무 종료일 (YYYY-MM-DD)

    // ====== 세션 공통 ======
    private String vendId;     // 회사코드
    private String loginEmpId; // 로그인 사원ID
}
