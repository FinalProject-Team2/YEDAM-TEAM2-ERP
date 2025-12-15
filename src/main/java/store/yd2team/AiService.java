package store.yd2team;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.common.util.LoginSession;
@Service
public class AiService {
   private final ChatClient chatClient;
   public AiService(ChatClient.Builder chatClientBuilder) {
       this.chatClient = chatClientBuilder.build();
   }
   // ✅ DB 기준 기반 리드점수 프롬프트(하드코딩 규칙 제거)
   private static final String LEAD_SCORE_BY_DB_PROMPT = """
       너는 리드(잠재고객) 점수 계산기다.
       사용자가 제공한 "기준 데이터(DB에서 온 목록)"를 최우선으로 사용해서 점수를 계산해야 한다.
       규칙:
       1) 기준 데이터는 여러 그룹(STDR_ID)으로 구성된다.
       2) 각 그룹에서 리드 정보와 가장 잘 맞는 항목 1개를 골라 그 INFO_SCORE를 점수로 쓴다.
       3) 해당 그룹에서 아무 것도 매칭 안 되면 그 그룹 점수는 0점이다.
       4) 모든 그룹 점수를 합산해서 최종 점수를 만든다.
       5) 최종 점수는 0~95 정수로 출력한다.
       6) 출력은 숫자 하나만. 설명/단어/단위 절대 금지.
       7) 만약 업종에 코딩, 분석, 전처리, 정보 이라는 키워드가 있을시 10점에서 25점까지 랜덤으로 점수를 추가한다.
       매칭 힌트:
       - 업종/지역은 문자열 포함(키워드) 기반으로 합리적으로 판단한다.
       - 회사규모는 문자열이 정확히 맞으면 그 항목을 선택한다.
       - 설립일은 'YYYY-MM-DD'면 현재 연도 기준으로 대략 년차를 추정해서 가장 가까운 항목을 선택한다.
       """;
   // 공통: 결과 숫자 파싱
   private int callScore(String userPrompt) {
       String result = chatClient.prompt()
               .system(LEAD_SCORE_BY_DB_PROMPT)
               .user(userPrompt)
               .call()
               .content()
               .trim();
       try {
           return Integer.parseInt(result);
       } catch (Exception e) {
           e.printStackTrace();
           return 0;
       }
   }
   // ✅ DB 기준 목록을 받아서 프롬프트를 만들고 점수 계산
   public int calculateLeadScoreByDbCriteria(
           String loginIndustry,
           String leadIndustry,
           String companySize,
           String region,
           String establishDate,
           List<PotentialStdrVO> stdrDetailList
   ) {
       if (stdrDetailList == null || stdrDetailList.isEmpty()) {
           return 0; // 기준 자체가 없으면 0점 처리
       }
       // STDR_ID별로 묶기
       Map<String, List<PotentialStdrVO>> grouped = stdrDetailList.stream()
               .collect(Collectors.groupingBy(PotentialStdrVO::getStdrId));
       // 보기 좋게 STDR_ID 정렬
       List<String> stdrIds = grouped.keySet().stream()
               .sorted()
               .toList();
       StringBuilder criteriaText = new StringBuilder();
       for (String stdrId : stdrIds) {
           criteriaText.append("[").append(stdrId).append(" 기준 항목]\n");
           List<PotentialStdrVO> items = grouped.get(stdrId).stream()
                   .sorted(Comparator.comparing(PotentialStdrVO::getStdrDetailId))
                   .toList();
           for (PotentialStdrVO item : items) {
               criteriaText.append("- ")
                       .append(item.getStdrIteamInfo())
                       .append(" = ")
                       .append(item.getInfoScore())
                       .append("\n");
           }
           criteriaText.append("\n");
       }
       String userPrompt = """
           [로그인 회사 업종코드]
           %s
           [리드 업종명]
           %s
           [리드 회사규모]
           %s
           [리드 지역/상권]
           %s
           [리드 설립일]
           %s
           [DB 기준 데이터]
           %s
           """.formatted(
               safe(loginIndustry),
               safe(leadIndustry),
               safe(companySize),
               safe(region),
               safe(establishDate),
               criteriaText.toString()
           );
       return callScore(userPrompt);
   }
   private String safe(String s) {
       return (s == null) ? "" : s;
   }
   // ✅ 기존 메서드는 "DB 기준 기반 계산"으로 갈아타기 (호출부 유지용)
   public int calculateLeadScoreByIndustry(
           String loginIndustry,
           String leadIndustry,
           String companySize,
           String region,
           String establishDate,
           List<PotentialStdrVO> stdrDetailList
   ) {
       // 세션 업종코드 우선 적용(너 기존 로직 유지)
       String bizType = LoginSession.getBizcnd();
       if (bizType != null && !bizType.isBlank()) {
           loginIndustry = bizType;
       }
       return calculateLeadScoreByDbCriteria(
               loginIndustry, leadIndustry, companySize, region, establishDate, stdrDetailList
       );
   }
}


