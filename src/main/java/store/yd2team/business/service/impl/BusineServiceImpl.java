package store.yd2team.business.service.impl;

import java.net.URI;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import store.yd2team.business.mapper.BusinessMapper;
import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.business.service.PublicDataResponse;
import store.yd2team.business.service.PublicDataRow;

@Service
@RequiredArgsConstructor
public class BusineServiceImpl implements BusinessService {

    private final BusinessMapper businessMapper;

    @Value("${publicdata.service-key}")
    private String serviceKey;  // 지금은 안 쓰고 하드코딩 중

    @Override
    public List<BusinessVO> getList() {
        return businessMapper.getList();
    }
    
    @Override
	public int existsPotentialInfoNo(Long potentialInfoNo) {
		return 0;
	}

    
    @Override
    public void fetchAndSaveFromApi() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String urlString = "https://api.odcloud.kr/api/15125657/v1/uddi:46ae6a57-03aa-4eef-9120-f632956d38e5";
            String encodedServiceKey = "qIWgK5nPmfUmVGffAxfyxZqboQb%2FwSG0TFq8Gu1GwkI2pLB13450nWdVnNDL%2BDjfCIakfDpJwi2yOqppnR%2Fbpw%3D%3D";

            URI uri = UriComponentsBuilder.fromUriString(urlString)
                    .queryParam("serviceKey", encodedServiceKey)
                    .queryParam("page", 1)
                    .queryParam("perPage", 50)
                    .build(true)
                    .toUri();

            System.out.println("최종 URL = " + uri);

            PublicDataResponse response = restTemplate.getForObject(uri, PublicDataResponse.class);

            System.out.println("API 원본 응답 = " + response);

            if (response == null || response.getData() == null) {
                System.out.println("응답이 없거나 data가 비어있음");
                return;
            }

            // 회사규모 랜덤 값 목록
            String[] companySizes = {"대기업", "준대기업", "중견기업", "중소기업", "강소기업"};
            Random random = new Random();


            for (PublicDataRow row : response.getData()) {
                BusinessVO vo = new BusinessVO();
                
                Long potentialInfoNo = row.getNo() != null ? row.getNo().longValue() : null;

                // 1) potential_info_no 중복체크
                int exists = businessMapper.existsPotentialInfoNo(potentialInfoNo);
                if (exists > 0) {
                    System.out.println("이미 있는 번호 → 스킵 : " + potentialInfoNo);
                    continue;
                }

                // 공공데이터에서 직접 매핑
                vo.setPotentialInfoNo(row.getNo().longValue()); // 번호
                vo.setVendNm(row.getVendNm());                  // 기업한글명
                vo.setEstablishDate(row.getEstablishDate());    // 설립일자 (yyyy-MM-dd)
                vo.setIndustryType(row.getCategoryType());      // 카테고리구분 → 업종 비슷한 느낌

                // 주소에서 region 가공 (서울 / 경기 / 부산 ...)
                vo.setRegion(extractRegion(row.getBaseAddress()));
                
                // 랜덤 회사규모
                vo.setCompanySize(companySizes[random.nextInt(companySizes.length)]);

                // 나머지 컬럼들
                vo.setVendId(null);         // 공공데이터에 별도 아이디 없음

                businessMapper.insertPotential(vo);
                businessMapper.getList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // "서울 영등포구 여의대로 14" → "서울"
    private String extractRegion(String baseAddress) {
        if (baseAddress == null || baseAddress.isEmpty()) {
            return null;
        }
        String[] parts = baseAddress.split(" ");
        return parts.length > 0 ? parts[0] : null;
    }

    //잠재고객검색조회
	@Override
	public List<BusinessVO> getBusinessList(BusinessVO vo) {
		 return businessMapper.getBusinessList(vo);
	}

	//잠재고객기준상세목록조회
	@Override
	 public List<PotentialStdrVO> getStdrDetailAll() {
        return businessMapper.getStdrDetailAll();
    }

	//잠재고객기준상세목록수정

    public void saveAll(List<BusinessVO> list) {
        if (list == null) return;

        for (BusinessVO vo : list) {

            // 1) 아예 내용이 없으면 건너뛰기
            String info = vo.getStdrIteamInfo();
            Integer score = vo.getInfoScore();
            boolean noInfo  = (info == null || info.trim().isEmpty());
            boolean noScore = (score == null);

            if (noInfo && noScore) {
                continue; // 아무 값도 없으면 skip
            }

            // 2) PK 없으면 INSERT, 있으면 UPDATE
            if (vo.getStdrId() == null || vo.getStdrId().trim().isEmpty()) {
            	businessMapper.insertDetail(vo);
            } else {
            	businessMapper.updateDetail(vo);
            }
        }
    }

    public List<BusinessVO> getListByCond(String condGb) {
        return businessMapper.selectByCondGb(condGb);
    }


	
	


	

}
