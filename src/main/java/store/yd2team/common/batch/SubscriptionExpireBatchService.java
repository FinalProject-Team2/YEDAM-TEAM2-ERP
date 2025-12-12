package store.yd2team.common.batch;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.mapper.EmpAcctMapper;
import store.yd2team.common.mapper.SubscriptionMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionExpireBatchService {

    private final SubscriptionMapper subscriptionMapper;
    private final EmpAcctMapper empAcctMapper;

    @Transactional
    public void expireSubscriptions() {

        List<String> expiredVendIds = subscriptionMapper.selectExpiredVendIds();

        if (expiredVendIds == null || expiredVendIds.isEmpty()) {
            log.info("[SUBS BATCH] 만료 대상 없음");
            return;
        }

        int totalUpdated = 0;

        for (String vendId : expiredVendIds) {
            int updated = empAcctMapper.updateAcctStByVendId(vendId, "r4", "BATCH");
            totalUpdated += updated;

            log.info("[SUBS BATCH] vendId={} -> tb_emp_acct st=r4 변경 rows={}", vendId, updated);
        }

        log.info("[SUBS BATCH] 완료: 만료 vend={}건, 계정 변경 rows={}",
                 expiredVendIds.size(), totalUpdated);
    }
}
