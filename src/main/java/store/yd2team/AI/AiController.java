package store.yd2team.AI;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.RequiredArgsConstructor;
import store.yd2team.AiService;
import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.common.util.LoginSession;
@Controller
@RequiredArgsConstructor
public class AiController {
   private final AiService aiService;
   private final BusinessService businessService;
   public record LeadScoreRequest(
           String leadIndustry,
           String companySize,
           String region,
           String establishDate
   ) {}
   @PostMapping("/ai/leadscore")
   @ResponseBody
   public int leadScore(@RequestBody LeadScoreRequest req) {
       // 로그인 회사 업종코드(세션)
       String loginIndustry = LoginSession.getBizcnd();
       // ✅ DB 기준 조회
       PotentialStdrVO cond = new PotentialStdrVO();
       List<PotentialStdrVO> stdrDetailList = businessService.getPotentialStdrDetailList(cond);
       // ✅ AI 점수 계산(기준표 반영)
       return aiService.calculateLeadScoreByIndustry(
               loginIndustry,
               req.leadIndustry(),
               req.companySize(),
               req.region(),
               req.establishDate(),
               stdrDetailList
       );
   }
}


