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
	
	// 신규 사원계정(emp_acct) 등록
	int insertVendAcct(store.yd2team.common.service.EmpAcctVO empAcct);
	
	// 해당 월의 최대 업체 ID 시퀀스 조회 (예: vend_202512001 → 001)
	String getMaxVendSeqOfMonth(@Param("prefix") String prefix);
	
	// 해당 월의 최대 사원계정 ID 시퀀스 조회 (예: mas_acct_202512001 → 001)
	String getMaxVendAcctSeqOfMonth(@Param("prefix") String prefix);
	
	int insertUserRole(
			@Param("vendId") String vendId,
		    @Param("empAcctId") String empAcctId,
		    @Param("roleId") String roleId,
		    @Param("creaBy") String creaBy
		);
	
	int countUserRole(@Param("vendId") String vendId,
            @Param("empAcctId") String empAcctId,
            @Param("roleId") String roleId);
	
	// 신규 마스터 사원(tb_emp) 등록
	int insertMasterEmp(store.yd2team.insa.service.EmpVO emp);
	
	// tb_emp용 다음 emp_id 조회 (EmpMapper.setDbAddId와 동일 쿼리 사용)
	String getNextEmpId();
	
}// end interface