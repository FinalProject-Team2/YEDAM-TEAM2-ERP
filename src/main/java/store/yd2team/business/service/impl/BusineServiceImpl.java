package store.yd2team.business.service.impl;
import java.net.URI;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.RequiredArgsConstructor;
import store.yd2team.AiService;
import store.yd2team.business.mapper.BusinessMapper;
import store.yd2team.business.service.BusinessService;
import store.yd2team.business.service.BusinessVO;
import store.yd2team.business.service.ChurnStdrVO;
import store.yd2team.business.service.ContactVO;
import store.yd2team.business.service.DemoVO;
import store.yd2team.business.service.LeadVO;
import store.yd2team.business.service.MonthlySalesDTO;
import store.yd2team.business.service.PotentialStdrVO;
import store.yd2team.business.service.PublicDataResponse;
import store.yd2team.business.service.PublicDataRow;
import store.yd2team.business.service.churnRiskVO;
import store.yd2team.common.util.LoginSession;
@Service
@RequiredArgsConstructor
public class BusineServiceImpl implements BusinessService {
	
  private final BusinessMapper businessMapper;
  private final AiService aiService;
 
 
 
  @Value("${publicdata.service-key}")
  private String encodedServiceKey;
 
  @Override
	public List<ChurnStdrVO> getChurnStdrList(ChurnStdrVO churn) {
		return businessMapper.getChurnStdrList(churn);
	}
 
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
//            String encodedServiceKey = "qIWgK5nPmfUmVGffAxfyxZqboQb%2FwSG0TFq8Gu1GwkI2pLB13450nWdVnNDL%2BDjfCIakfDpJwi2yOqppnR%2Fbpw%3D%3D";
          URI uri = UriComponentsBuilder.fromUriString(urlString)
                  .queryParam("serviceKey", encodedServiceKey)
                  .queryParam("page", 1)
                  .queryParam("perPage", 10)
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
  // 잠재고객조건 + 리드점수 포함 조회 (BusinessController에서 사용하는 메서드라고 가정)
  @Override
  public List<BusinessVO> getBusinessList(BusinessVO cond) {
  	
  	// 1) 조건에 맞는 잠재고객 리스트 조회
      List<BusinessVO> list = businessMapper.getBusinessList(cond);
      // 2) 로그인 회사 업종 가져오기
      String loginIndustry = getLoginCompanyIndustry();
      // 3) 각 잠재고객 업종과 비교해서 리드점수 계산
      for (BusinessVO row : list) {
          String leadIndustry = row.getIndustryType(); // 잠재고객 업종 컬럼
          String companySize    = row.getCompanySize();
          String region         = row.getRegion();
          String establishDate  = row.getEstablishDate();
        
          //todo upd
          int score =0;  //aiService.calculateLeadScoreByIndustry(loginIndustry, leadIndustry,  companySize,  region,   establishDate);
          row.setLeadScore(score);
      }
      return list;
  }
   // 임시 구현: 나중에는 로그인한 회사 정보(세션/DB)에서 업종을 가져오도록 수정
  private String getLoginCompanyIndustry() {
  	String biz = LoginSession.getBizcnd();
      return biz;
  }
  //
  //
  // 잠재고객 기준상세목록 전제조회
  public List<PotentialStdrVO> getPotentialStdrDetailList(PotentialStdrVO cond) {
      return businessMapper.getPotentialStdrDetailList(cond);
  }
  // 잠재고객 기준상세목록 등록 및 수정 - I/U 통합 저장
  public void savePotentialStdrDetailList(List<PotentialStdrVO> list) {
      for (PotentialStdrVO row : list) {
          if ("I".equals(row.getRowStatus())) {
       	   businessMapper.insertPotentialStdrDetail(row);
          } else if ("U".equals(row.getRowStatus())) {
       	   businessMapper.updatePotentialStdrDetail(row);
          }
          }
      }
  // 잠재고객 기준상세목록 삭제
  @Override
  public int deletePotentialStdrList(List<String> idList) {
      int cnt = 0;
      for (String id : idList) {
          businessMapper.deletePotentialStdr(id);
          cnt++;
      }
      return cnt;
  }
	@Override
	public List<BusinessVO> getcustaddrtype(String cond) {
		 List<BusinessVO> list = businessMapper.getcustaddrtype(cond);
		    String loginIndustry = LoginSession.getBizcnd(); // 로그인 회사 업종
		    for (BusinessVO row : list) {
//		    	 int score = aiService.calculateLeadScoreByIndustry(
//		                 loginIndustry,
//		                 row.getIndustryType(),
//		                 row.getCompanySize(),
//		                 row.getRegion(),
//		                 row.getEstablishDate()
//		         );
		    	int score = 0;
		         row.setLeadScore(score);
		    }
		    return list;
	}
	//
	//
	//휴면,이탈검색조회
	@Override
	public List<churnRiskVO> getchurnRiskList(churnRiskVO vo) {
		return businessMapper.getchurnRiskList(vo);
	}
	//휴면,이탈 평균구매주기
	@Override
	public int getAVG() {
		return businessMapper.getAVG();
	}
	//매풀변동
	@Override
	public List<MonthlySalesDTO> getMonthlySalesChange(MonthlySalesDTO vo) {
		return businessMapper.getMonthlySalesChange(vo);
	}
	//
	//
	//
	//접촉사항조회
	@Override
	public List<ContactVO> getAction() {
		return businessMapper.getAction();
	}
	//
	//진짜 접촉사항 조회
	@Override
   public List<ContactVO> getContactListByVend(String vendId) {
       return businessMapper.selectContactListByVend(vendId);
   }
	//
	// 접촉내역 저장
   @Override
   @Transactional
   public void saveAll(String vendId, Integer potentialInfoNo, List<ContactVO> contactList) {
       if (contactList == null) return;
       for (ContactVO row : contactList) {
           row.setVendId(vendId);
           row.setPotentialInfoNo(potentialInfoNo);
			if( row.getContactNo() == null  || row.getContactNo().equals("") ) {
				businessMapper.insertContact(row);
			}
			else {  
				businessMapper.updateContact(row);
			}
          
       }
   }
   //
	//리드내역 조회
	@Override
	public List<LeadVO> getLeadListByVend(String vendId) {
		return businessMapper.getLeadListByVend(vendId);
	}
	//리드내역 저장
	@Override
	@Transactional
	public void saveAllLead(String vendId, Integer potentialInfoNo, List<LeadVO> list) {
		
       if (list == null) return;
       for (LeadVO row : list) {
           row.setVendId(vendId);
           row.setPotentialInfoNo(potentialInfoNo);
          
			if( row.getLeadNo()  == null  || row.getLeadNo().equals("") ) {
				businessMapper.insertLead(row);
			}
			else {  
				businessMapper.updateLead(row);
				
			}
       }
   }
	//
	//데모내역 조회
	@Override
	public List<DemoVO> getDemoListByVend(String vendId) {
		return businessMapper.getDemoListByVend(vendId);
	}
	//데모내역 저장
	@Override
	@Transactional
	public void saveAllDemo(String vendId, Integer potentialInfoNo, List<DemoVO> demolist) {
		
       if (demolist == null) return;
       for (DemoVO row : demolist) {
           row.setVendId(vendId);
           row.setPotentialInfoNo(potentialInfoNo);
          
			if( row.getDemoQuotatioNo()  == null  || row.getDemoQuotatioNo().equals("") ) {
				businessMapper.insertDemo(row);
			}
			else {  
				businessMapper.updateDemo(row);
				
			}
       }
   }
	
	
	
}

