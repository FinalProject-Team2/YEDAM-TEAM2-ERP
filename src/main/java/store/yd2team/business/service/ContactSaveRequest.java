package store.yd2team.business.service;

import java.util.List;

import lombok.Data;

@Data
public class ContactSaveRequest {
    private String vendId;               // 거래처ID
    private Integer potentialInfoNo;     // 잠재정보번호

    private List<ContactVO> contactList; // 접촉내역
    private List<LeadVO>    leadList;    // 리드내역
    private List<DemoVO>    demoList;    // 데모내역
}
