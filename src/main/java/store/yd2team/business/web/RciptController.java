package store.yd2team.business.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.RciptService;
import store.yd2team.business.service.RciptVO;

@Controller
@RequestMapping("/rcipt")
@RequiredArgsConstructor
public class RciptController {

    private final RciptService rciptService;

    /**
     * 미수채권관리 메인 화면
     */
    @GetMapping("/rciptMain")
    public String rciptMain(Model model) {
        // 필요 시 공통코드/콤보 데이터 모델에 담아서 내려주기
        return "business/rcipt";   // templates/business/atmpt.html
    }
    
    // 조회
    @PostMapping("/list")
    @ResponseBody
    public List<RciptVO> getRciptList(@RequestBody RciptVO searchVO) {
        return rciptService.searchRcipt(searchVO);
    }

   
    // 입금처리
    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@RequestBody RciptVO vo) {

        Map<String, Object> result = new HashMap<>();

        try {
            rciptService.saveRcipt(vo);
            result.put("success", true);
        } catch (Exception e) {

            String msg = e.getMessage();

            // ORA 에러 메시지 정리
            if (msg != null && msg.contains("ORA-20003")) {
                msg = "입금금액이 채권잔액을 초과할 수 없습니다.";
            } else if (msg != null && msg.contains("ORA-20002")) {
                msg = "입금금액이 올바르지 않습니다.";
            } else if (msg != null && msg.contains("ORA-20001")) {
                msg = "입금 대상 채권이 존재하지 않습니다.";
            } else {
                msg = "입금 처리 중 오류가 발생했습니다.";
            }

            result.put("success", false);
            result.put("message", msg);
        }

        return result;
    }
	/*
	 * @PostMapping("/save")
	 * 
	 * @ResponseBody public Map<String, Object> saveRcipt(@RequestBody RciptVO vo) {
	 * 
	 * Map<String, Object> result = new HashMap<>();
	 * 
	 * try { rciptService.saveRcipt(vo); result.put("success", true);
	 * result.put("message", "입금 처리가 완료되었습니다.");
	 * 
	 * } catch (RuntimeException e) { // 👉 업무 에러 / 프로시저 에러 전부 여기로
	 * result.put("success", false); result.put("message", e.getMessage());
	 * 
	 * } catch (Exception e) { result.put("success", false); result.put("message",
	 * "입금 처리 중 오류가 발생했습니다."); }
	 * 
	 * return result; }
	 */
    
    // 입금상세내역 조회
    @GetMapping("/detail/list")
    @ResponseBody
    public List<RciptVO> selectRciptDetailList(
            @RequestParam("rciptId") String rciptId) {

        if (rciptId == null || rciptId.isEmpty()) {
            throw new IllegalArgumentException("채권 ID가 없습니다.");
        }

        return rciptService.selectRciptDetailList(rciptId);
    }
}
