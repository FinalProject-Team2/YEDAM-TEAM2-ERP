package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.service.EmpAcctVO;

@Mapper
public interface EmpLoginMapper {

	// 로그인(회사코드 , 로그인 ID로 조회)
	EmpAcctVO selectByLogin(@Param("vendId") String vendId,
			                @Param("loginId") String loginId);
	
	// 로그인 성공
	int updateLoginSuccess(@Param("empAcctId") String empAcctId,
			               @Param("updtBy") String updtBy);
	
	// 로그인 실패
	int updateLoginFail(@Param("empAcctId") String empAcctId,
			            @Param("maxFailCnt") int maxFailCnt,
			            @Param("updtBy") String updtBy);
	
	// 로그인 자동 잠금 해제
	int unlock(@Param("empAcctId") String empAcctId,
			   @Param("updtBy") String updtBy);
	
	List<String> selectRoleIdsByEmpAcctId(@Param("empAcctId") String empAcctId);
	
	List<String> selectAuthCodesByEmpAcctId(@Param("empAcctId") String empAcctId);
	
	EmpAcctVO selectByEmpAcctId(@Param("empAcctId") String empAcctId);
	
	EmpAcctVO selectOprtrByLogin(@Param("loginId") String loginId);
	
	List<MenuAuthDto> selectAllActiveMenus();
	
	int countByLoginId(@Param("loginId") String loginId);
}
