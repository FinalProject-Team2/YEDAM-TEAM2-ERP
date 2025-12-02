package store.yd2team;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import store.yd2team.common.util.LoginSession;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // =========================
    // 1) 공통 역할 프롬프트 (예제용)
    // =========================
    private static final String ROLE_TEACHER = """
        너는 초등학생에게 설명하는 친절한 선생님이야.
        어려운 단어를 쓰지 말고, 쉬운 예시를 들어서 설명해줘.
        너무 길게 말하지 말고, 5문장 이내로 답해줘.
        """;

    public String explainForKid(String question) {
        return chatClient.prompt()
                .system(ROLE_TEACHER)
                .user(question)
                .call()
                .content();
    }

    // =========================
    // 2) 업종 리드점수 계산용 시스템 프롬프트
    // =========================
    private static final String INDUSTRY_LEAD_SCORE_PROMPT = """
        너는 한국표준산업분류(KSIC)를 잘 아는 업종 분석 전문가야.

        아래에 "소분류 업종 목록"과 "중분류 업종 목록"이 주어질 거야.
        각 목록에는 업종 이름과 간단한 설명이 들어가 있다.

        [소분류 업종 목록]
        01 농업 
        02 임업 
        03 어업 
        05 석탄 원유 및 천연가스 광업 
        06 금속 광업 
        07 비금속광물 광업 연료용 제외 
        08 광업 지원 서비스업 
        10 식료품 제조업 
        11 음료 제조업 
        12 담배 제조업 
        13 섬유제품 제조업 의복 제외 
        14 의복 의복 액세서리 및 모피제품 제조업 
        15 가죽 가방 및 신발 제조업 
        16 목재 및 나무제품 제조업 가구 제외 
        17 펄프 종이 및 종이제품 제조업 
        18 인쇄 및 기록매체 복제업 
        19 코크스 연탄 및 석유정제품 제조업 
        20 화학 물질 및 화학제품 제조업 의약품 제외 
        21 의료용 물질 및 의약품 제조업 
        22 고무 및 플라스틱제품 제조업 
        23 비금속 광물제품 제조업 
        24 1차 금속 제조업 
        25 금속 가공제품 제조업 기계 및 가구 제외 
        26 전자 부품 컴퓨터 영상 음향 및 통신장비 제조업 
        27 의료 정밀 광학 기기 및 시계 제조업 
        28 전기장비 제조업 
        29 기타 기계 및 장비 제조업 
        30 자동차 및 트레일러 제조업 
        31 기타 운송장비 제조업 
        32 가구 제조업 
        33 기타 제품 제조업 
        34 산업용 기계 및 장비 수리업 
        35 전기 가스 증기 및 공기 조절 공급업 
        36 수도업 
        37 하수 폐수 및 분뇨 처리업 
        38 폐기물 수집 운반 처리 및 원료 재생업 
        39 환경 정화 및 복원업 
        41 종합 건설업 
        42 전문직별 공사업 
        45 자동차 및 부품 판매업 
        46 도매 및 상품 중개업 
        47 소매업 
        49 육상 운송 및 파이프라인 운송업 
        50 수상 운송업 
        51 항공 운송업 
        52 창고 및 운송관련 서비스업 
        55 숙박업 
        56 음식점 및 주점업 
        58 출판업 
        59 영상 오디오 기록물 제작 및 배급업 
        60 방송업 
        61 우편 및 통신업 
        62 컴퓨터 프로그래밍 시스템 통합 및 관리업 
        63 정보서비스업 
        64 금융업 
        65 보험 및 연금업 
        66 금융 및 보험관련 서비스업 
        68 부동산업 
        70 연구개발업 
        71 전문 서비스업 
        72 건축 기술 엔지니어링 및 기타 과학기술 서비스업 
        73 기타 전문 과학 및 기술 서비스업 
        74 사업시설 관리 및 조경 서비스업 
        75 사업 지원 서비스업 
        76 임대업 부동산 제외 
        84 공공 행정 국방 및 사회보장 행정 
        85 교육 서비스업 
        86 보건업 
        87 사회복지 서비스업 
        90 창작 예술 및 여가관련 서비스업 
        91 스포츠 및 오락관련 서비스업 
        94 협회 및 단체 
        95 개인 및 소비용품 수리업 
        96 기타 개인 서비스업
        97 가구 내 고용활동 
        98 달리 분류되지 않은 자가 소비를 위한 가구의 재화 및 서비스 생산활동 
        99 국제 및 외국기관

        [중분류 업종 목록]
        농업, 임업 및 어업(01~03) 
        광업(05~08) 
        제조업(10~34) 
        전기, 가스, 증기 및 공기 조절 공급업(35) 
        수도, 하수 및 폐기물 처리, 원료 재생업(36~39) 
        건설업(41~42) 
        도매 및 소매업(45~47) 
        운수 및 창고업(49~52) 
        숙박 및 음식점업(55~56) 
        정보통신업(58~63) 
        금융 및 보험업(64~66) 
        부동산업(68) 
        전문, 과학 및 기술 서비스업(70~73) 
        사업시설 관리, 사업 지원 및 임대 서비스업(74~76) 
        공공 행정, 국방 및 사회보장 행정(84) 
        교육 서비스업(85) 
        보건업 및 사회복지 서비스업(86~87) 
        예술, 스포츠 및 여가관련 서비스업(90~91) 
        협회 및 단체, 수리 및 기타 개인 서비스업(94~96) 
        가구 내 고용활동 및 달리 분류되지 않은 자가 소비 생산활동(97~98) 
        국제 및 외국기관(99)

        너에게 들어오는 입력은 다음 두 가지야.
        1) 로그인한 회사의 업종 (문자열)
        2) 잠재고객(리드)의 업종 (문자열)이지만 숫자를 전달할거야

        너는 이 두 업종이 위의 소분류/중분류 목록에서
        어떤 그룹에 속하는지 최대한 합리적으로 판단해서
        아래 규칙에 따라 리드 점수를 결정해야 한다.

        [점수 규칙]
        - 두 업종이 같은 "소분류"에 속하면: 25점
        - 같은 소분류는 아니지만, 같은 "중분류" 그룹에 속하면: 15점
        - 그 외(관련성이 낮으면): 0점

        매우 중요:
        - 너는 결과로 "숫자 하나만" 출력해야 한다.
          25 또는 15 또는 0 중 하나만 출력해라.
        - 설명 문장, 이유, 단위(점) 같은 건 절대 쓰지 마라.
          예: "25"만 출력 ( "25점", "점수는 25입니다" 등 금지 )
        """;

    // =========================
    // 3) 프롬프트 하나를 받아서 숫자 점수로 바꿔주는 공통 메서드
    // =========================
    public int calculateLeadScoreByPrompt(String userPrompt) {
        String result = chatClient
                .prompt()
                .system(INDUSTRY_LEAD_SCORE_PROMPT)
                .user(userPrompt)
                .call()
                .content()
                .trim();

        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            // 혹시 AI가 이상한 문자열을 내보내면 방어적으로 0점 처리
            return 0;
        }
    }

    /**
     * 로그인 회사 업종과 리드 업종을 받아
     * - 소분류 일치 → 25
     * - 중분류만 일치 → 15
     * - 그 외 → 0
     * 을 AI 프롬프트로 계산해서 리턴하는 메서드
     */
    public int calculateLeadScoreByIndustry(String loginIndustry, String leadIndustry) {

        if (loginIndustry == null || loginIndustry.isBlank()
                || leadIndustry == null || leadIndustry.isBlank()) {
            return 0;
        }
//
//         나중에 세션에서 꺼내고 싶으면 여기서 loginIndustry를 덮어써도 됨.
//         예)
         String bizType = LoginSession.getBizcnd();
         loginIndustry = bizType;

        String userPrompt = """
            [로그인 회사 업종]
            %s

            [리드(잠재고객) 업종]
            %s
            """.formatted(loginIndustry, leadIndustry);
        
        System.out.println(bizType + loginIndustry);

        return calculateLeadScoreByPrompt(userPrompt);
    }

    // =========================
    // 4) 그냥 단순 질의용 메서드
    // =========================
    public String ask(String message) {
        if (message == null || message.isBlank()) {
            return "질문이 비어 있어.";
        }
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
