package store.yd2team.insa.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 공공데이터포털 SpcdeInfoService.getRestDeInfo
 * 응답 JSON 구조에 맞춘 DTO
 */
@Data
public class HolidayApiResponse {

    private Response response;

    @Data
    public static class Response {
        private Body body;
    }

    @Data
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    public static class Items {

        // item 이 배열일 때도 있고, 하나의 객체일 때도 있어서
        // 둘 다 List<Item> 으로 받을 수 있게 설정
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Item> item;
    }

    /**
     * 공휴일 한 건 정보
     */
    @Data
    public static class Item {

        /** 날짜 (예: 20250203 → 2025-02-03) */
        private String locdate;

        /** 명칭 (예: 설날, 삼일절) */
        private String dateName;

        /** 공공기관 휴일 여부 (Y / N) */
        private String isHoliday;

        /** 특일 종류 (01, 02 등) – 필요하면 사용 */
        private String dateKind;

        /** 순번 */
        private String seq;
    }
}
