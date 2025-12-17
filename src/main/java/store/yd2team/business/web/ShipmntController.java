package store.yd2team.business.web;

import java.util.List;

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

@Controller
@RequestMapping("/shipment")
@RequiredArgsConstructor
public class ShipmntController {
	
	private final ShipmntService shipmntService;

	@GetMapping("/shipmentMain")
	public String selectall(Model model) {
		return "business/shipment";
	}
	
	// 견적서 조회(그리드)
    @PostMapping("/list")
    @ResponseBody
    public List<ShipmntVO> shipmentList(@RequestBody ShipmntVO cond) {
        return shipmntService.selectShipmntList(cond);
    }
}