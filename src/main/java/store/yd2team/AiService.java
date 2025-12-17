package store.yd2team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.common.util.LoginSession;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // AI는 "선택"만 한다. 점수 합산은 서버가 한다.
    private static final String PICK_DETAIL_IDS_PROMPT = """
        너는 리드(잠재고객) 점수 평가를 위한 "분류기"다.
        사용자가 제공한 [DB 기준 데이터]만 근거로 삼아, 각 STDR_ID 그룹에서 가장 적합한 항목 1개를 고른다.

        출력 규칙(매우 중요):
        - 반드시 JSON만 출력한다. 설명/문장/코드블록/추가 텍스트 금지.
        - 형식은 정확히 아래와 같아야 한다.
          {"pickedDetailIds":["STDR-001.1","STDR-002.3","STDR-003.2","STDR-004.5"]}
        - 각 STDR_ID 그룹(STDR-001, STDR-002, STDR-003, STDR-004...)에서 1개씩 선택한다.
        - 매칭되는 게 없으면 그 그룹은 선택하지 말고 넘어간다(즉, 배열에 넣지 않는다).
        - 반드시 [DB 기준 데이터]에 실제로 존재하는 STDR_DETAIL_ID만 반환한다.

        매칭 힌트:
        - 업종/지역은 "문자열 포함" 기준으로 가장 그럴듯한 항목을 선택
        - 회사규모는 문자열이 정확히 일치하는 항목 우선
        - 설립일은 'YYYY-MM-DD' 또는 'YYYY-MM-DD HH:MI:SS'가 올 수 있으며, 현재 연도 기준으로 년차를 추정해 "N년이상" 중 가장 적합한 항목 선택
        """;

    // 1) AI 호출해서 JSON 받기
    private String callPickJson(String userPrompt) {
        return chatClient.prompt()
                .system(PICK_DETAIL_IDS_PROMPT)
                .user(userPrompt)
                .call()
                .content()
                .trim();
    }

    // 2) AI JSON 파싱해서 detailId 리스트로
    private List<String> parsePickedDetailIds(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            Object v = map.get("pickedDetailIds");
            if (v instanceof List<?> l) {
                List<String> out = new ArrayList<>();
                for (Object o : l) {
                    if (o != null) out.add(o.toString());
                }
                return out;
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 3) DB 기준 목록에서 detailId로 점수 찾기
    private int scoreByDetailId(List<PotentialStdrVO> stdrDetailList, String detailId) {
        if (detailId == null) return 0;
        for (PotentialStdrVO v : stdrDetailList) {
            if (detailId.equals(v.getStdrDetailId())) {
                return toInt(v.getInfoScore());
            }
        }
        return 0;
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    // ✅ 이게 너가 원하는 방식(기준+잠재고객 -> AI에 프롬프트 -> 선택 -> 서버합산)
    public int calculateLeadScoreByDbCriteria(
            String loginIndustry,
            String leadIndustry,
            String companySize,
            String region,
            String establishDate,
            List<PotentialStdrVO> stdrDetailList
    ) {
        if (stdrDetailList == null || stdrDetailList.isEmpty()) return 0;

        // 기준 데이터는 STDR_ID별로 묶어서 AI가 "그룹당 1개" 고르기 쉽게 구성
        Map<String, List<PotentialStdrVO>> grouped = stdrDetailList.stream()
                .collect(Collectors.groupingBy(PotentialStdrVO::getStdrId));

        // AI가 detailId를 정확히 고르도록, stdrDetailId를 반드시 같이 준다
        StringBuilder criteriaText = new StringBuilder();
        grouped.keySet().stream().sorted().forEach(stdrId -> {
            criteriaText.append("[").append(stdrId).append("]\n");
            List<PotentialStdrVO> items = grouped.get(stdrId).stream()
                    .sorted((a, b) -> safe(a.getStdrDetailId()).compareTo(safe(b.getStdrDetailId())))
                    .toList();
            for (PotentialStdrVO item : items) {
                criteriaText.append("- STDR_DETAIL_ID=").append(safe(item.getStdrDetailId()))
                        .append(" | ITEM=").append(safe(item.getStdrIteamInfo()))
                        .append(" | SCORE=").append(item.getInfoScore())
                        .append("\n");
            }
            criteriaText.append("\n");
        });

        String userPrompt = """
            [로그인 회사 업종코드]
            %s
            [잠재고객 업종]
            %s
            [잠재고객 회사규모]
            %s
            [잠재고객 지역/상권]
            %s
            [잠재고객 설립일]
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

        String aiJson = callPickJson(userPrompt);
        List<String> picked = parsePickedDetailIds(aiJson);

        int total = 0;
        for (String detailId : picked) {
            total += scoreByDetailId(stdrDetailList, detailId);
        }

        // (원하면 유지) 업종 키워드 보너스
        if (leadIndustry != null) {
            String s = leadIndustry;
            if (s.contains("코딩") || s.contains("분석") || s.contains("전처리") || s.contains("정보")) {
                total += 15;
            }
        }

        if (total < 0) total = 0;
        if (total > 100) total = 100;
        return total;
    }

    // 호출부 유지용(너 서비스에서 이 메서드 쓰고 있으니 그대로 둠)
    public int calculateLeadScoreByIndustry(
            String loginIndustry,
            String leadIndustry,
            String companySize,
            String region,
            String establishDate,
            List<PotentialStdrVO> stdrDetailList
    ) {
        String bizType = LoginSession.getBizcnd();
        if (bizType != null && !bizType.isBlank()) {
            loginIndustry = bizType;
        }
        return calculateLeadScoreByDbCriteria(
                loginIndustry, leadIndustry, companySize, region, establishDate, stdrDetailList
        );
    }
}
