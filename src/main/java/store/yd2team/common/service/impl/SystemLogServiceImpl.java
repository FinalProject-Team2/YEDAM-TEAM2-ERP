package store.yd2team.common.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.dto.SysLogSearchCond;
import store.yd2team.common.mapper.SysLogMapper;
import store.yd2team.common.service.SysLogVO;
import store.yd2team.common.service.SystemLogService;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SysLogMapper sysLogMapper;

    @Override
    public void writeLog(SessionDto session,
                         String moduleCode,
                         String action,
                         String targetTable,
                         String targetPk,
                         String summary) {

        if (session == null) {
            // 로그인 전(예: 로그인 실패 로그) 등은 필요하면 별도 전략
            return;
        }

        SysLogVO log = new SysLogVO();

        log.setEmpAcctId(session.getEmpAcctId());
        log.setVendId(session.getVendId());
        log.setModlGrp("MD");        // 나중에 공통코드로 교체
        log.setModlCode(moduleCode); // COMMON / HR / SALES...

        log.setTargetTbNm(targetTable);
        log.setTargetPk(targetPk);
        log.setMotionTy(action);
        log.setSmry(summary);
        log.setYn("e1");

        log.setCreaBy(session.getEmpId());
        log.setUpdtBy(session.getEmpId());

        sysLogMapper.insertLog(log);
    }

    @Override
    public List<SysLogVO> getLogList(SysLogSearchCond cond) {
        return sysLogMapper.selectLogList(cond);
    }
    
    @Override
    public List<String> getLoginIdList(String keyword) {
        return sysLogMapper.selectLoginIdList(keyword);
    }

    @Override
    public List<CodeDto> getCodeList(String grpId) {
        return sysLogMapper.selectCodeList(grpId);
    }
}
