package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class ContactVO {

    private Integer contactNo;           // 접촉ID (PK)
    private Integer potentialInfoNo;     // 잠재정보번호 (FK)
    private String vendId;               // 거래처 ID
    private String empId;               // 거래처 ID
    private String contactDt;            // 접촉날짜
    private String contactManager;       // 담당자
    private String contactWay;           // 접촉방법
    private String contactCntn;          // 접촉내용

    private Date creaDt;                 // 생성일시
    private String creaBy;               // 생성자
    private Date updtDt;                 // 수정일시
    private String updtBy;               // 수정자
}
