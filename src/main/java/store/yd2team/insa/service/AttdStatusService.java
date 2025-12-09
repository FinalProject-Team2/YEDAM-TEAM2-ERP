package store.yd2team.insa.service;

import java.util.List;

public interface AttdStatusService {

    // 근태 목록 조회 (관리자/일반자 자동 분기)
    List<AttdStatusVO> selectAttdStatusList(AttdStatusVO searchVO);

    // 현재 로그인 사용자가 HR 관리자 권한인지 여부
    boolean isHrAdminCurrentUser();
}
