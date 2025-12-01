package store.yd2team.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignUpMapper {
	
	// login_id 중복 카운트 조회
	int countLoginId(@Param("loginId") String loginId);
	
	// 사업자등록번호(bizno) 중복 카운트 조회
	int countBizNo(@Param("bizNo") String bizNo);
	
	// 신규 업체(vend) 등록
	int insertVend(store.yd2team.common.service.VendVO vend);
	
	// 신규 업체계정(vend_acct) 등록
	int insertVendAcct(store.yd2team.common.service.VendAcctVO vendAcct);
	
	// 해당 월의 최대 업체 ID 시퀀스 조회 (예: vend_202512001 → 001)
	String getMaxVendSeqOfMonth(@Param("prefix") String prefix);
	
	// 해당 월의 최대 업체계정 ID 시퀀스 조회 (예: vend_acct_202512001 → 001)
	String getMaxVendAcctSeqOfMonth(@Param("prefix") String prefix);
	
}// end interface