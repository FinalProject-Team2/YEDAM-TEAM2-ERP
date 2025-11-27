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
import store.yd2team.business.service.CommonCodeVO;
import store.yd2team.business.service.PriceService;
import store.yd2team.business.service.PriceVO;


@Controller
@RequestMapping("/price")
@RequiredArgsConstructor
public class PriceController {

	private final PriceService priceService;

	@GetMapping("/manage")
	public String selectall(Model model) {

		model.addAttribute("test", "testone");
		return "business/priceManage";

	}

	@PostMapping("/list")
	@ResponseBody
	public List<PriceVO> getPriceList(@RequestBody PriceVO vo) {
		
		System.out.println("검색조건 >>> " + vo.toString());
		
		return priceService.getPricePolicyList(vo);
	}
	
	@GetMapping("/type-codes")
	@ResponseBody
	public List<CommonCodeVO> getPriceType() {
	    return priceService.getPriceType();
	}
	
}
