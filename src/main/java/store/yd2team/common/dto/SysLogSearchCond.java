package store.yd2team.common.dto;

import lombok.Data;

@Data
public class SysLogSearchCond {
    private String accountId;   // 화면의 "계정 ID"
    private String module;      // COMMON / HR / SALES
    private String action;      // LOGIN / INSERT / UPDATE / DELETE
    private String startDate;   // yyyy-MM-dd
    private String endDate;     // yyyy-MM-dd
}
