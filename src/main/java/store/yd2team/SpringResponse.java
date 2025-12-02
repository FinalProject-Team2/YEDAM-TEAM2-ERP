//package com.example.demo;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class SpringResponse {
//
//    @Autowired
//    private ChatClient.Builder chatClientBuilder;
//
//    private ChatClient chatClient;
//
//    // 공통 역할 프롬프트
//    private static final String ROLE_TEACHER = """
//        너는 초등학생에게 설명하는 친절한 선생님이야.
//        어려운 단어를 쓰지 말고, 쉬운 예시를 들어서 설명해줘.
//        """;
//
//    @BeforeEach
//    void setUp() {
//        this.chatClient = chatClientBuilder.build();
//    }
//
//    @Test
//    public void testSimpleQuote() {
//        String response = chatClient.prompt("스티브 잡스의 명언을 한 개 알려줘")
//                .call()
//                .content();
//
//        System.out.println(response);
//    }
//
//    @Test
//    public void testExplainForKid() {
//        String input = "블랙홀은 뭐야?";
//
//        String summary = chatClient.prompt()
//                .system(ROLE_TEACHER)  // 미리 넣어둔 역할
//                .user(input)           // 사용자 질문
//                .call()
//                .content();
//
//        System.out.println(summary);
//    }
//
//    @Test
//    public void testIndustrySummary() {
//        String industryRaw = """
//            이 회사는 클라우드 기반 재무 회계 솔루션을 제공하며,
//            중소기업 고객을 대상으로 구독형 SaaS 서비스를 운영하고 있다.
//            """;
//
//        String summaryPrompt = """
//            다음 기업의 업종 설명을 영업 담당자도 이해하기 쉽게
//            핵심만 3줄로 요약해줘.
//
//            [업종 설명]
//            %s
//            """.formatted(industryRaw);
//
//        String summary = chatClient.prompt()
//                .user(summaryPrompt)
//                .call()
//                .content();
//
//        System.out.println(summary);
//    }
//}
