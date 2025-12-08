package store.yd2team.business.service.impl;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.AtmptMapper;
import store.yd2team.business.service.AtmptService;
import store.yd2team.business.service.AtmptVO;
@Service
@RequiredArgsConstructor
public class AtmptServiceImpl implements AtmptService {
   private final AtmptMapper atmptMapper;
   // 검색조건(조회)
   @Override
   public List<AtmptVO> searchAtmpt(AtmptVO vo) {
       return AtmptMapper.searchAtmpt(vo);
   }
   // 검색조건(저장)
   @Override
   public int saveAtmpt(AtmptVO vo) throws Exception {
       System.out.println("### Service saveAtmpt 호출 ###");
       int result = atmptMapper.insertAtmpt(vo);
       System.out.println("### result = " + result);
       return 1;
   }
   // 조회 고객사 auto complete(고객코드, 고객사명)
   @Override
   public List<AtmptVO> searchCustcomId(String keyword) {
       return AtmptMapper.searchCustcomId(keyword);
   }
   @Override
   public List<AtmptVO> searchCustcomName(String keyword) {
       return AtmptMapper.searchCustcomName(keyword);
   }



}