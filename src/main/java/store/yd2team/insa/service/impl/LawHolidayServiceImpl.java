package store.yd2team.insa.service.impl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.yd2team.insa.mapper.WkTyMapper;
import store.yd2team.insa.service.LawHolidayService;

@Service
@RequiredArgsConstructor
public class LawHolidayServiceImpl implements LawHolidayService {

    private final WkTyMapper wkTyMapper;

    @Value("${publicdata.holiday.service-key}")
    private String serviceKey;

    // JSON 파싱용
    private final ObjectMapper objectMapper = new ObjectMapper();

 
    @Override
    public void fetchAndSaveHolidays(int year) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 공공데이터포털 법정 공휴일 API URL
            String baseUrl = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

            // 1월~12월까지 반복
            for (int month = 1; month <= 12; month++) {
                String solMonth = String.format("%02d", month);

                URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .queryParam("ServiceKey", serviceKey)
                        .queryParam("solYear", year)
                        .queryParam("solMonth", solMonth)
                        .queryParam("_type", "json")
                        .build(true)
                        .toUri();

                System.out.println("휴일 API 호출 URL = " + uri);

                // 🔹 DTO로 바로 받지 말고, 문자열로 먼저 받아서 직접 파싱
                String json = restTemplate.getForObject(uri, String.class);

                if (json == null || json.isBlank()) {
                    System.out.println("응답이 비어 있음 (year=" + year + ", month=" + month + ")");
                    continue;
                }

                JsonNode root = objectMapper.readTree(json);
                JsonNode bodyNode = root.path("response").path("body");
                JsonNode itemsNode = bodyNode.path("items");

                // 🔹 공휴일이 없는 달일 경우 items 가 "" (문자열) 인 경우가 있어서 처리
                if (itemsNode.isMissingNode() || itemsNode.isNull() || itemsNode.isTextual()) {
                    System.out.println("해당 월에 공휴일 없음 (year=" + year + ", month=" + month + ")");
                    continue;
                }

                JsonNode itemNode = itemsNode.path("item");
                if (itemNode.isMissingNode() || itemNode.isNull()) {
                    System.out.println("item 노드 없음 (year=" + year + ", month=" + month + ")");
                    continue;
                }

                // 🔹 item 이 배열일 수도 있고, 객체 한 개일 수도 있음
                if (itemNode.isArray()) {
                    for (JsonNode node : itemNode) {
                        saveOneHoliday(node);
                    }
                } else if (itemNode.isObject()) {
                    saveOneHoliday(itemNode);
                } else {
                    System.out.println("item 형식이 예상과 다름 (year=" + year + ", month=" + month + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 공휴일 1건 저장 로직
     */
    private void saveOneHoliday(JsonNode node) {
        String isHoliday = node.path("isHoliday").asText();
        if (!"Y".equalsIgnoreCase(isHoliday)) {
            // 공휴일로 표시 안 된 건 스킵
            return;
        }

        String locdate = node.path("locdate").asText();    // 예: "20260216"
        String dateName = node.path("dateName").asText();  // 예: "설날"

        if (locdate == null || locdate.isBlank()) {
            return;
        }

        // 이미 같은 날짜(법정, vend_id IS NULL)가 있으면 스킵
        int exists = wkTyMapper.existsLegalHlDyByLocdate(locdate);
        if (exists > 0) {
            System.out.println("이미 등록된 법정 공휴일 → 스킵 : " + locdate + " / " + dateName);
            return;
        }

        // INSERT
        wkTyMapper.insertLegalHlDyFromApi(dateName, locdate);
        System.out.println("법정 공휴일 등록 : " + locdate + " / " + dateName);
    }
}
