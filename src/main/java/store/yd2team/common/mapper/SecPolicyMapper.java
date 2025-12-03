package store.yd2team.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.service.SecPolicyVO;

@Mapper
public interface SecPolicyMapper {
	// 거래처 별 보안 정책 조회
	SecPolicyVO selectByVendId(@Param("vendId") String vendId);
	
	// vendId 존재 여부 확인
    int countByVendId(@Param("vendId") String vendId);
    
    // 등록
    int insertPolicy(SecPolicyVO vo);
    
    // 수정
    int updatePolicy(SecPolicyVO vo);
    
    // 공통 기본 정책 조회용
    SecPolicyVO selectDefaultPolicy();
}
