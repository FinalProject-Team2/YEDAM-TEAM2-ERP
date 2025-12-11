package store.yd2team.common.service;

import java.util.List;

import store.yd2team.common.dto.AutoCompleteItemDto;
import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.LockAccountDto;
import store.yd2team.common.dto.LockAccountSearchDto;
import store.yd2team.common.dto.LockAccountUpdateDto;

public interface LockAccountService {

    List<CodeDto> getEmpAcctStatusCodes();

    List<LockAccountDto> getLockAccountList(LockAccountSearchDto search);

    int updateAccountStatuses(List<LockAccountUpdateDto> list, String loginId);

    List<AutoCompleteItemDto> findVendAutoComplete(String keyword);

    List<AutoCompleteItemDto> findAccountIdAutoComplete(String keyword);

    List<AutoCompleteItemDto> findUserNameAutoComplete(String keyword);
}
