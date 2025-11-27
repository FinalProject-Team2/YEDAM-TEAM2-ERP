// 공공데이터 한 행(row) 구조
package store.yd2team.business.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PublicDataRow {

    // ↓↓↓ 여기 JsonProperty 값은 실제 JSON 키 이름 보고 꼭 바꿔라
    @JsonProperty("기업명")
    private String vendNm;

    @JsonProperty("사업자등록번호")
    private String vendId;

    @JsonProperty("업종")
    private String industryType;

    @JsonProperty("기업규모")
    private String companySize;

    @JsonProperty("지역")
    private String region;

    @JsonProperty("설립일자")  // 예: "2018-03-10"
    private String establishDate;
}
