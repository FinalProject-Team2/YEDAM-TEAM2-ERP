package store.yd2team.business.service.impl;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.BusinessMapper;
import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.PublicDataResponse;
import store.yd2team.business.service.PublicDataRow;

@Service
@RequiredArgsConstructor
public class BusineServiceImpl implements BusinessService {

	private final BusinessMapper businessMapper;

	@Value("${publicdata.service-key}")
	private String serviceKey;

	@Override
	public List<BusinessVO> getList() {
		return businessMapper.getList();
	}

	@Override
	public void fetchAndSaveFromApi() {

		try {
			
			RestTemplate restTemplate = new RestTemplate();
			
			String urlString = "https://api.odcloud.kr/api/15125657/v1/uddi:46ae6a57-03aa-4eef-9120-f632956d38e5";
			String serviceKey = "qIWgK5nPmfUmVGffAxfyxZqboQb%2FwSG0TFq8Gu1GwkI2pLB13450nWdVnNDL%2BDjfCIakfDpJwi2yOqppnR%2Fbpw%3D%3D";  //인코딩
			
			URI uri = UriComponentsBuilder.fromUriString(urlString)
			        .queryParam("serviceKey", serviceKey)  
			        .queryParam("page", 1)
			        .queryParam("perPage", 10)
			        .build(true)     // 인코딩 여부. 인코딩을 해야하면 false 
			        .toUri();

			System.out.println("최종 URL = " + uri);


			// ★★ 핵심 ★★
			// URL을 RESTTemplate이 다시 인코딩하지 않도록 "ResponseEntity<String>" 로 먼저 받는다
			PublicDataResponse response = restTemplate.getForObject(uri, PublicDataResponse.class);

			System.out.println("API 원본 응답 = " + response);

			// JSON → 객체 변환

			int rank = 1;
			for (PublicDataRow row : response.getData()) {
				BusinessVO vo = new BusinessVO();
				vo.setPotentialCondNo(null);
				vo.setVendId(row.getVendId());
				vo.setRank(rank++);
				vo.setVendNm(row.getVendNm());
				vo.setIndustryType(row.getIndustryType());
				vo.setCompanySize(row.getCompanySize());
				vo.setRegion(row.getRegion());
				vo.setEstablishDate(row.getEstablishDate());
				businessMapper.insertPotential(vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
