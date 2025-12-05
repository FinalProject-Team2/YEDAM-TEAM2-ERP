package store.yd2team.business.service.impl;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.CreditMapper;
import store.yd2team.business.service.CreditService;
import store.yd2team.business.service.CreditVO;
import store.yd2team.business.service.CustcomVO;
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
   private final CreditMapper creditMapper;
   // 검색조건(조회)
   @Override
   public List<CreditVO> searchCredit(CreditVO vo) {
       return creditMapper.searchCredit(vo);
   }
   // 검색조건(저장)
   @Override
   public int saveCredit(CreditVO vo) throws Exception {
       System.out.println("### Service saveCredit 호출 ###");
       int result = creditMapper.insertCredit(vo);
       System.out.println("### result = " + result);
       return 1;
   }
   // 조회 고객사 auto complete(고객코드, 고객사명)
   @Override
   public List<CreditVO> searchCustcomId(String keyword) {
       return creditMapper.searchCustcomId(keyword);
   }
   @Override
   public List<CreditVO> searchCustcomName(String keyword) {
       return creditMapper.searchCustcomName(keyword);
   }
   // 업체정보 모달창
   @Override
   public CustcomVO getCustcomDetail(String custcomId) {
       return creditMapper.selectCustcomDetail(custcomId);
   }
   // ======================================================
   //  ✅ 여신 평가 기능 추가
   // ======================================================
   @Override
   public int evaluateCredit(CreditVO vo) throws Exception {
       // 1) 평가 대상 고객 목록 조회 (조건: custcomId, custcomName)
       List<CreditVO> targetList = creditMapper.selectEvalTarget(vo);
       if (targetList == null || targetList.isEmpty()) {
           return 0;
       }
       int updatedCount = 0;
       // 2) 각 고객별 평가
       for (CreditVO target : targetList) {
           // 2-1) 고객별 여신 상태 조회 (연체개월수, 잔액, 한도 등)
           CreditVO eval = creditMapper.selectCreditStatus(target);
           if (eval == null) continue;
           // ===========================================
           //  2-2) 악성여신 판정 로직
           //     - maxOverdueMm > creditMm → 악성여신 Y
           // ===========================================
           String badYn = "N";
           if (eval.getMaxOverdueMm() > eval.getCreditMm()) {
               badYn = "Y";
           }
           // ===========================================
           //  2-3) 출하정지 판단 (예: 악성여신이면 출하정지)
           // ===========================================
           String shipHoldYn = "N";
           if ("Y".equals(badYn)) {
               shipHoldYn = "Y";
           }
           // ===========================================
           //  2-4) 회전일수 계산
           //      turnoverDays = remainAmt / (최근 3개월 평균매출/일수)
           //      여기서는 Mapper에서 계산된 값만 사용한다고 가정
           // ===========================================
           int turnoverDays = eval.getTurnoverDays(); // Mapper에서 계산된 값을 사용
           // 2-5) 결과 업데이트
           eval.setBadCreditYn(badYn);
           eval.setShipHoldYn(shipHoldYn);
           eval.setTurnoverDays(turnoverDays);
           int update = creditMapper.updateCreditEval(eval);
           updatedCount += update;
       }
       return updatedCount;
   }
}



