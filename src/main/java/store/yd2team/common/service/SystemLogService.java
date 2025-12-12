package store.yd2team.common.service;

import java.util.List;

import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.dto.SysLogSearchCond;

public interface SystemLogService {

    // 1) 로그 기록
    void writeLog(SessionDto session,
                  String moduleCode,
                  String action,
                  String targetTable,
                  String targetPk,
                  String summary);

    // 2) 로그 조회
    List<SysLogVO> getLogList(SysLogSearchCond cond);
    
    List<String> getLoginIdList(String keyword);
    
    List<CodeDto> getCodeList(String grpId);
}
