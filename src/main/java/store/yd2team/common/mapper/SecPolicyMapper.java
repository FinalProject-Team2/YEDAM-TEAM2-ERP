package store.yd2team.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.service.SecPolicyVO;

@Mapper
public interface SecPolicyMapper {
	// 거래처 별 보안 정책 조회
	SecPolicyVO selectByVendId(@Param("vendId") String vendId);
}
