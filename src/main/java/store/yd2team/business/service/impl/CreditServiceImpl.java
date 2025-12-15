package store.yd2team.business.service.impl;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.CreditMapper;
import store.yd2team.business.service.AtmptVO;
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
       List<CreditVO> targetList = creditMapper.searchCredit(vo);
       if (targetList == null || targetList.isEmpty()) {
           return 0;
       }
       int updatedCount = 0;
       // 2) 각 고객별 평가
       for (CreditVO target : targetList) {
           // 2-1) 고객별 여신 상태 조회 (연체개월수, 여신개월, 잔액, 한도 등)
           CreditVO eval = creditMapper.selectCreditStatus(target);
           if (eval == null) continue;
           
           // ===========================================
           //  2-2) 악성여신(출하정지) 판정 로직
           //     - maxOverdueMm > creditMm → 악성여신 Y
           // ===========================================
           String shipHoldYn = "N";
           if (eval.getMaxOverdueMm() > eval.getCreditMm()) {
        	   shipHoldYn = "Y";
           }
           
           // ===========================================
           //  2-4) 회전일수 계산
           //      turnoverDays = remainAmt / (최근 3개월 평균매출/일수)
           //      여기서는 Mapper에서 계산된 값만 사용한다고 가정
           // ===========================================
           int turnoverDays = eval.getTurnoverDays(); // Mapper에서 계산된 값을 사용
           // 2-5) 결과 업데이트
           eval.setShipmntStop(shipHoldYn);
           eval.setTurnoverDays(turnoverDays);
           int update = creditMapper.updateCreditEval(eval);
           updatedCount += update;
       }
       return updatedCount;
   }
   
// 저장버튼이벤트
   @Override
   @Transactional
   public void saveCreditLimit(List<CreditVO> list) {

       for (CreditVO vo : list) {

           // 🔹 null 방어
           if (vo.getCustcomId() == null) {
               continue;
           }

           // 🔹 여신체크가 N이면 한도 0 처리 (실무 룰)
           if ("N".equals(vo.getCdtlnCheck())) {
               vo.setMrtggLmt(0L);
               vo.setCreditLmt(0L);
               vo.setCdtlnLmt(0L);
           }

           creditMapper.updateCreditLimit(vo);
       }
   }
   
@Override
public int insertCdtlnLmt(CreditVO vo) {
	// TODO Auto-generated method stub
    creditMapper.insertCdtlnLmt(vo);
    
    AtmptVO  avo = new AtmptVO();
    avo.setCdtlnNo(vo.getCdtlnNo());
    avo.setCustcomId(vo.getCustcomId());
    avo.setVendId(vo.getVendId());
	avo.setAtmptBlce(0l);
    return creditMapper.insertAtmpt(avo);
	
}
@Override
public int insertAtmpt(AtmptVO vo) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public int updateShipmnt(CreditVO vo) {
	return creditMapper.updateShipmnt(vo);
	
}
}



