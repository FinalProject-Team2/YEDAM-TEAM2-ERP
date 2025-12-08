package store.yd2team.business.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.ContactSaveRequest;
import store.yd2team.business.service.ContactVO;

@RestController
public class ContactController {

    @Autowired
    BusinessService businessService;
    
    // 거래처ID(vend_id) 기준 접촉내역 조회 (AJAX)
    @GetMapping("/salesActivity/contactByVend")
    public List<ContactVO> getContactListByVend(@RequestParam("vendId") String vendId) {
        System.out.println("=== contactByVend 호출, vendId = " + vendId + " ===");
        return businessService.getContactListByVend(vendId);
    }
    
    // 접촉내역 전체 저장 (삭제 후 다시 INSERT 방식)
    @PostMapping("/salesActivity/contact/saveAll")
    public String saveAllContacts(@RequestBody ContactSaveRequest req) {

        System.out.println("=== saveAllContacts 호출 ===");
        System.out.println("vendId = " + req.getVendId()
                         + ", potentialInfoNo = " + req.getPotentialInfoNo());

        businessService.saveAll(
            req.getVendId(),
            req.getPotentialInfoNo(),
            req.getContactList()
        );

        return "OK";
    }
    
//    // 거래처ID(vend_id) 기준 리드내역 조회 (AJAX)
//    @GetMapping("/salesActivity/contactByVend")
//    public List<ContactVO> getLeadListByVend(@RequestParam("vendId") String vendId) {
//        System.out.println("=== contactByVend 호출, vendId = " + vendId + " ===");
//        return businessService.getContactListByVend(vendId);
//    }
//    
//    // 접촉내역 전체 저장 (삭제 후 다시 INSERT 방식)
//    @PostMapping("/salesActivity/contact/saveAll")
//    public String saveAllLead(@RequestBody ContactSaveRequest req) {
//
//        System.out.println("=== saveAllContacts 호출 ===");
//        System.out.println("vendId = " + req.getVendId()
//                         + ", potentialInfoNo = " + req.getPotentialInfoNo());
//
//        businessService.saveAll(
//            req.getVendId(),
//            req.getPotentialInfoNo(),
//            req.getContactList()
//        );
//
//        return "OK";
//    }
    
}
