package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.SysLogSearchCond;
import store.yd2team.common.service.SysLogVO;

@Mapper
public interface SysLogMapper {

    void insertLog(SysLogVO log);

    List<SysLogVO> selectLogList(SysLogSearchCond cond);
    
    List<String> selectLoginIdList(@Param("keyword") String keyword);

    List<CodeDto> selectCodeList(@Param("grpId") String grpId);
}
