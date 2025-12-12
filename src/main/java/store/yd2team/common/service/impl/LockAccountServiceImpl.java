package store.yd2team.common.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.aop.SysLog;
import store.yd2team.common.aop.SysLogConfig;
import store.yd2team.common.dto.AutoCompleteItemDto;
import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.LockAccountDto;
import store.yd2team.common.dto.LockAccountSearchDto;
import store.yd2team.common.dto.LockAccountUpdateDto;
import store.yd2team.common.mapper.LockAccountMapper;
import store.yd2team.common.service.LockAccountService;

@Service
@RequiredArgsConstructor
@SysLogConfig(module = "d2", table = "TB_EMP_ACCT", pkParam = "empAcctId")
public class LockAccountServiceImpl implements LockAccountService {

 private final LockAccountMapper lockAccountMapper;

 @Override
 public List<CodeDto> getEmpAcctStatusCodes() {
     return lockAccountMapper.selectEmpAcctStatusCodes();
 }

 @Override
 public List<LockAccountDto> getLockAccountList(LockAccountSearchDto search) {
     return lockAccountMapper.selectLockAccountList(search);
 }

 @Override
 @SysLog(action = "ac2", msg = "잠금계정 상태 변경", pkFromSession = true, pkField = "empAcctId")
 public int updateAccountStatuses(List<LockAccountUpdateDto> list, String loginId) {
     if (CollectionUtils.isEmpty(list)) {
         return 0;
     }

     int result = 0;
     for (LockAccountUpdateDto dto : list) {
         dto.setUpdtBy(loginId);
         result += lockAccountMapper.updateAccountStatus(dto);
     }
     return result;
 }

 @Override
 public List<AutoCompleteItemDto> findVendAutoComplete(String keyword) {
     return lockAccountMapper.selectVendAutoComplete(keyword);
 }

 @Override
 public List<AutoCompleteItemDto> findAccountIdAutoComplete(String keyword) {
     return lockAccountMapper.selectAccountIdAutoComplete(keyword);
 }

 @Override
 public List<AutoCompleteItemDto> findUserNameAutoComplete(String keyword) {
     return lockAccountMapper.selectUserNameAutoComplete(keyword);
 }
}
