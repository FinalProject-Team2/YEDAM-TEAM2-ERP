package store.yd2team.AI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.AiService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    // GET /ai/chat : 채팅 화면
    @GetMapping("/chat")
    public String chatPage() {
        return "ai/chat";
    }

    // POST /ai/chat : 폼 전송으로 사용할 때 (Thymeleaf form)
    @PostMapping("/chat")
    public String chat(@RequestParam("message") String message, Model model) {
        String answer = aiService.ask(message);
        model.addAttribute("userMessage", message);
        model.addAttribute("answer", answer);
        return "ai/chat";
    }

    // REST API 형식: POST /ai/api/chat  (JSON으로 쓸 때)
    @PostMapping("/api/chat")
    @ResponseBody
    public AiResponse chatApi(@RequestBody AiRequest request) {
        String answer = aiService.ask(request.message());
        return new AiResponse(answer);
    }
    
    @PostMapping("/explain")
    public String explain(@RequestParam String question, Model model) {

        // 사용자는 그냥 question만 입력
        String answer = aiService.explainForKid(question);

        model.addAttribute("question", question);
        model.addAttribute("answer", answer);
        return "ai/explain"; // Thymeleaf 템플릿
    }

    public record AiRequest(String message) {}
    public record AiResponse(String answer) {}
}
