package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.AttdStatusVO;

@Mapper
public interface AttdStatusMapper {

    int selectHrAdminCnt(@Param("vendId") String vendId,
                         @Param("empAcctId") String empAcctId);

    List<AttdStatusVO> selectAttdListAdmin(AttdStatusVO vo);
    List<AttdStatusVO> selectAttdListUser(AttdStatusVO vo);
}
