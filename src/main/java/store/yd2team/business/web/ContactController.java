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
import store.yd2team.business.service.DemoVO;
import store.yd2team.business.service.LeadVO;

@RestController
public class ContactController {

    @Autowired
    BusinessService businessService;
    
    // 잠재고객 거래처ID(vend_id) 기준 접촉내역 조회 (AJAX)
    @GetMapping("/salesActivity/contactByVend")
    public List<ContactVO> getContactListByVend(@RequestParam("vendId") String vendId) {
        System.out.println("=== contactByVend 호출, vendId = " + vendId + " ===");
        return businessService.getContactListByVend(vendId);
    }
    
    // 모든내역 전체 저장
    @PostMapping("/salesActivity/contact/saveAll")
    public String saveAllContacts(@RequestBody ContactSaveRequest req) {

        System.out.println("=== saveAllContacts 호출 ===");
        System.out.println("vendId = " + req.getVendId()
                         + ", potentialInfoNo = " + req.getPotentialInfoNo());
        //접촉내역 저장
        businessService.saveAll(
            req.getVendId(),
            req.getPotentialInfoNo(),
            req.getContactList()
        );
        
        //리드내역 저장
        businessService.saveAllLead(
                req.getVendId(),
                req.getPotentialInfoNo(),
                req.getLeadList()
            );
        
        //데모내역 저장
        businessService.saveAllDemo(
                req.getVendId(),
                req.getPotentialInfoNo(),
                req.getDemoList()
            );

        return "OK";
    }
    
    // 거래처ID(vend_id) 기준 리드내역 조회 (AJAX)
    @GetMapping("/salesActivity/LeadByVend")
    public List<LeadVO> getLeadListByVend(@RequestParam("vendId") String vendId) {
        System.out.println("=== leadByVend 호출, vendId = " + vendId + " ===");
        return businessService.getLeadListByVend(vendId);
    }
    
    // 거래처ID(vend_id) 기준 데모내역 조회 (AJAX)
    @GetMapping("/salesActivity/DemoByVend")
    public List<DemoVO> getDemoListByVend(@RequestParam("vendId") String vendId) {
        System.out.println("=== demoByVend 호출, vendId = " + vendId + " ===");
        return businessService.getDemoListByVend(vendId);
    }
}
