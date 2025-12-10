//package store.yd2team.business.web;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import store.yd2team.business.service.BusinessService;
//import store.yd2team.business.service.PotentialStdrVO;
//
//@Controller
////@RequestMapping("/potential")
//public class PotentialStdrDetailController {
//
////    @Autowired
////    BusinessService businessService;
//    
////    // 1) 페이지 오픈
////    @GetMapping("/potentialCustRegister")
////    public String potentialStdrDetailPage() {
////        return "business/potentialCustRegister";
////    }
//
////    // 상세 전체 조회 (그리드 4개 공통)
////    @RequestMapping("/potentialCustRegister/get")
////    @ResponseBody
////    public List<PotentialStdrVO> getPotentialStdrDetailList(PotentialStdrVO cond) {
////        return businessService.getPotentialStdrDetailList(cond);
////    }
////    // 상세 저장
////    @RequestMapping("/potentialCustRegister/save")
////    @ResponseBody
////    public String savePotentialStdrDetailList(@RequestBody List<PotentialStdrVO> list) {
////    	businessService.savePotentialStdrDetailList(list);
////        return "OK";
////    }
////}
