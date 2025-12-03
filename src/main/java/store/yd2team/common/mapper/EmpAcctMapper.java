package store.yd2team.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmpAcctMapper {

	// 비밀번호 변경
    int updatePassword(@Param("empAcctId") String empAcctId,
                       @Param("loginPwd") String loginPwd,
                       @Param("updtBy") String updtBy);

    // 임시 비밀번호 플래그 해제
    int clearTempPasswordFlag(@Param("empAcctId") String empAcctId,
                              @Param("updtBy") String updtBy);
}
