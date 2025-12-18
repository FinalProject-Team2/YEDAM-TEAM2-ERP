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

   
   // 여신 회전일수 배치(batch)실행
   @Override
   @Transactional
   public void runTurnoverBatch() {

       String updtBy = "batch";

       System.out.println(">>> [SERVICE] runTurnoverBatch start");

       int updatedRows =
               creditMapper.updateTurnoverAndPolicyBatch(updtBy);

       System.out.println(">>> [SERVICE] updated rows = " + updatedRows);
       System.out.println(">>> [SERVICE] runTurnoverBatch end");
   }
   
   
   // 저장버튼이벤트
   @Override
   @Transactional
   public void saveCreditLimit(List<CreditVO> list) {

       for (CreditVO vo : list) {

           // null 방어
           if (vo.getCustcomId() == null) {
               continue;
           }

           // 여신체크가 N이면 한도 0 처리 (실무 룰)
           if ("N".equals(vo.getCdtlnCheck())) {
               vo.setMrtggLmt(0L);
               vo.setCreditLmt(0L);
               vo.setCdtlnLmt(0L);
           }

           creditMapper.updateCreditLimit(vo);
       }
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



