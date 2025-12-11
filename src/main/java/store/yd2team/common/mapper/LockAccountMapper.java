package store.yd2team.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.common.dto.AutoCompleteItemDto;
import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.LockAccountDto;
import store.yd2team.common.dto.LockAccountSearchDto;
import store.yd2team.common.dto.LockAccountUpdateDto;

@Mapper
public interface LockAccountMapper {

    // 계정 상태 코드 (grp_id = 'r')
    List<CodeDto> selectEmpAcctStatusCodes();

    // 잠금 계정 목록 조회
    List<LockAccountDto> selectLockAccountList(LockAccountSearchDto search);

    // 계정 상태 업데이트 (한 건씩)
    int updateAccountStatus(LockAccountUpdateDto dto);

    // 자동완성 - 거래처 명
    List<AutoCompleteItemDto> selectVendAutoComplete(@Param("keyword") String keyword);

    // 자동완성 - 계정 ID (마스터 계정만)
    List<AutoCompleteItemDto> selectAccountIdAutoComplete(@Param("keyword") String keyword);

    // 자동완성 - 사용자 이름 (마스터 계정만, tb_emp join)
    List<AutoCompleteItemDto> selectUserNameAutoComplete(@Param("keyword") String keyword);
}
