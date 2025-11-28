package store.yd2team.business.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.service.CustcomService;
import store.yd2team.business.service.CustcomVO;

@Controller
@RequestMapping("/custcom")
@RequiredArgsConstructor
public class CustcomController {

    private final CustcomService custcomService;

    @PostMapping("/list")
    @ResponseBody
    public List<CustcomVO> searchCustcom(@RequestBody CustcomVO vo) {
        return custcomService.searchCustcom(vo);
    }
}
