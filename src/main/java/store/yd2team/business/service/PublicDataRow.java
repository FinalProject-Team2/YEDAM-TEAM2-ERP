package store.yd2team.business.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // JSON에 더 많은 컬럼 있어도 무시
public class PublicDataRow {

    // 번호
    @JsonProperty("번호")
    private Integer no;

    // 기업한글명 -> 우리 DB의 vend_nm
    @JsonProperty("기업한글명")
    private String vendNm;

    // 설립일자 -> yyyy-MM-dd
    @JsonProperty("설립일자")
    private String establishDate;

    // 기본주소 -> "서울 영등포구 여의대로 14"
    @JsonProperty("기본주소")
    private String baseAddress;

    // 상세주소 -> "10층"
    @JsonProperty("상세주소")
    private String detailAddress;

    // 카테고리 구분
    @JsonProperty("카테고리구분")
    private String categoryType;

    // 필요하면 추가 가능
    // @JsonProperty("링크(URL)")
    // private String linkUrl;
}