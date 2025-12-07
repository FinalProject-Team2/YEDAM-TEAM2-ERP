package store.yd2team.business.service;

import java.util.Date;

import lombok.Data;

@Data
public class leadGenererVO {

    private Integer lead_dt;          // 접촉ID (PK)
    private Integer lead_manager;     // 잠재정보번호 (FK)
    private Date 	required_dt;      // 거래처 ID
    private String 	competitor_yn;    // 접촉날짜
    private String 	budget_avail_yn;  // 담당자
    private String 	leadscore;        // 접촉방법
    private String 	outcontact;       // 접촉내용

}
