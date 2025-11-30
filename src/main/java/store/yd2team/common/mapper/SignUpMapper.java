package store.yd2team.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignUpMapper {
	
	// login_id 중복 카운트 조회
	int countLoginId(@Param("loginId") String loginId);
	
	// 사업자등록번호(bizno) 중복 카운트 조회
	int countBizNo(@Param("bizNo") String bizNo);

}// end interface