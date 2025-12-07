package store.yd2team.business.service;

import java.util.List;

import lombok.Data;

@Data
public class ContactSaveRequest {
    private String vendId;
    private Integer potentialInfoNo;
    private List<ContactVO> contactList;
}
