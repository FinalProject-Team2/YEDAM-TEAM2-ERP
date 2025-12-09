package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.EmpAcctEmployeeDto;
import store.yd2team.common.dto.EmpDeptDto;
import store.yd2team.common.service.EmpAcctVO;

@Mapper
public interface EmpAcctMapper {

	// 비밀번호 변경
    int updatePassword(@Param("empAcctId") String empAcctId,
                       @Param("loginPwd") String loginPwd,
                       @Param("updtBy") String updtBy);

    // 임시 비밀번호 플래그 해제
    int clearTempPasswordFlag(@Param("empAcctId") String empAcctId,
                              @Param("updtBy") String updtBy);
    
    List<EmpAcctEmployeeDto> selectEmpEmployeeList(
            @Param("vendId")   String vendId,
            @Param("deptName") String deptName,
            @Param("jobName")  String jobName,
            @Param("empName")  String empName,
            @Param("loginId")  String loginId
    );
    
    List<EmpDeptDto> selectEmpDeptList(@Param("vendId") String vendId);
    
    // 🔹 자동완성: 사원 이름
    List<EmpAcctEmployeeDto> selectEmpNameAutoComplete(
            @Param("vendId")  String vendId,
            @Param("keyword") String keyword
    );

    // 🔹 자동완성: 계정 ID
    List<EmpAcctEmployeeDto> selectLoginIdAutoComplete(
            @Param("vendId")  String vendId,
            @Param("keyword") String keyword
    );
    
    EmpAcctVO selectByEmpAcctId(@Param("empAcctId") String empAcctId);

    EmpAcctVO selectByVendAndEmp(@Param("vendId") String vendId,
                                 @Param("empId")  String empId);
    
    int insertEmpAcct(EmpAcctVO vo);

    int updateEmpAcct(EmpAcctVO vo);
    
    String selectEmpPhone(@Param("vendId") String vendId,
            			  @Param("empId")  String empId);
}
