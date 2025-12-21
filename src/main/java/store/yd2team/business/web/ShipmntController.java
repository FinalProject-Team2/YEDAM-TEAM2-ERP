package store.yd2team.business.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.ShipmntService;
import store.yd2team.business.service.ShipmntVO;
import store.yd2team.common.util.LoginSession;

@Controller
@RequestMapping("/shipment")
@RequiredArgsConstructor
public class ShipmntController {

    private final ShipmntService shipmntService;

    @GetMapping("/shipmentMain")
    public String selectall(Model model) {
        return "business/shipment";
    }

    // 출하목록 조회
    @PostMapping("/list")
    @ResponseBody
    public List<ShipmntVO> shipmentList(@RequestBody ShipmntVO cond) {
        return shipmntService.selectShipmntList(cond);
    }

    // 출하처리
    @PostMapping("/complete")
    @ResponseBody
    public ResponseEntity<Void> completeShipment(@RequestBody List<String> oustIds) {
        String oustIdsCsv = String.join(",", oustIds);

        shipmntService.completeShipment(
            oustIdsCsv,
            LoginSession.getVendId(),
            LoginSession.getEmpId(),
            LoginSession.getLoginId()
        );

        return ResponseEntity.ok().build();
    }

	
	 // ✅ 출하취소 (프론트와 URL 일치)
	  
	 @PostMapping("/cancel")
	 @ResponseBody
	 public ResponseEntity<?> cancelShipment(@RequestBody ShipmntVO dto) {
		  
		 shipmntService.cancelShipment(
		        dto.getOustId(),
		        dto.getCancelReason(),
		        LoginSession.getVendId(),
		        LoginSession.getLoginId()
		    );
		 
			/*
			 * shipmntService.cancelShipment( dto.getOustId(), LoginSession.getVendId(),
			 * LoginSession.getEmpId(), LoginSession.getLoginId() );
			 */
		 
		 return ResponseEntity.ok().body( java.util.Map.of("message", "출하 취소 처리되었습니다.") );
	 
	 }
	 
}
