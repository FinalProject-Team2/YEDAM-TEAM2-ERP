package store.yd2team.common.web;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.yd2team.common.consts.SessionConst;
import store.yd2team.common.dto.AutoCompleteItemDto;
import store.yd2team.common.dto.CodeDto;
import store.yd2team.common.dto.LockAccountDto;
import store.yd2team.common.dto.LockAccountSearchDto;
import store.yd2team.common.dto.LockAccountUpdateDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.service.LockAccountService;

@Slf4j
@RestController
@RequestMapping("/api/lockAcct")
@RequiredArgsConstructor
public class UnlockAcctController {

    private final LockAccountService lockAccountService;

    // 1) 계정 상태 공통 코드
    @GetMapping("/status-codes")
    public List<CodeDto> getStatusCodes() {
        return lockAccountService.getEmpAcctStatusCodes();
    }

    // 2) 잠금 계정 목록 조회
    @GetMapping("/list")
    public List<LockAccountDto> getLockAccounts(
            @RequestParam(name = "clientName",  required = false) String clientName,
            @RequestParam(name = "accountId",   required = false) String accountId,
            @RequestParam(name = "userName",    required = false) String userName,
            @RequestParam(name = "status",      required = false) String status,
            @RequestParam(name = "lockStartDt", required = false) String lockStartDt,
            @RequestParam(name = "lockEndDt",   required = false) String lockEndDt
    ) {
        LockAccountSearchDto search = new LockAccountSearchDto();
        search.setClientName(clientName);
        search.setAccountId(accountId);
        search.setUserName(userName);
        search.setStatus(status);
        search.setLockStartDt(lockStartDt);
        search.setLockEndDt(lockEndDt);

        return lockAccountService.getLockAccountList(search);
    }

    // 3) 상태 변경(잠금 해제 포함) 저장
    @PostMapping("/update-status")
    public int updateStatuses(@RequestBody List<LockAccountUpdateDto> list,
                              HttpSession session) {

        SessionDto sessionDto = (SessionDto) session.getAttribute(SessionConst.LOGIN_EMP);
        String loginId = (sessionDto != null) ? sessionDto.getLoginId() : null;

        int updated = lockAccountService.updateAccountStatuses(list, loginId);
        log.info("LockAccount update-status count={}", updated);
        return updated;
    }

    // 4) 자동완성 - 거래처 명
    @GetMapping("/auto/vendor")
    public List<AutoCompleteItemDto> autoVendor(@RequestParam("keyword") String keyword) {
        return lockAccountService.findVendAutoComplete(keyword);
    }

    // 5) 자동완성 - 계정 ID
    @GetMapping("/auto/accountId")
    public List<AutoCompleteItemDto> autoAccountId(@RequestParam("keyword") String keyword) {
        return lockAccountService.findAccountIdAutoComplete(keyword);
    }

    // 6) 자동완성 - 사용자 이름
    @GetMapping("/auto/userName")
    public List<AutoCompleteItemDto> autoUserName(@RequestParam("keyword") String keyword) {
        return lockAccountService.findUserNameAutoComplete(keyword);
    }
}
