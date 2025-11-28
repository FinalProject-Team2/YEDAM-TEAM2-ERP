// 공공데이터 포털 공통 응답
package store.yd2team.business.service;

import java.util.List;

import lombok.Data;

@Data
public class PublicDataResponse {
    private int page;
    private int perPage;
    private int totalCount;
    private int currentCount;
    private List<PublicDataRow> data;
}