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
  // 세션에서 회사 코드(VEND_ID) 가져오기
 private String getLoginVendId() {
     String vendId = LoginSession.getVendId();
     if (vendId == null || vendId.isEmpty()) {
         throw new IllegalStateException("로그인 회사 코드(VEND_ID)가 없습니다.");
     }
     return vendId;
 }
 // 세션에서 사원 ID 가져오기
 private String getLoginEmpId() {
     String empId = LoginSession.getEmpId();
     if (empId == null || empId.isEmpty()) {
         throw new IllegalStateException("로그인 사원 ID가 없습니다.");
     }
     return empId;
 }
  //휴면,이탈 기준조회(완)
 @Override
	public List<ChurnStdrVO> getChurnStdrList(ChurnStdrVO churn) {
	  	churn.setVendId(getLoginVendId());
	  	System.out.println("vend_id 나온 거 이거임 =" + getLoginVendId());
	  	System.out.println("churn 나온 거 이거임 =" + churn);
		return businessMapper.getChurnStdrList(churn);
	}
 //휴면,이탈 기준 수정(완)
 @Override
 @Transactional
 public int updateChurnStdrList(
         List<ChurnStdrVO> dormancyList,
         List<ChurnStdrVO> churnList,
         String vendId,
         String empId
 ) {
     if (vendId == null || vendId.isEmpty()) vendId = getLoginVendId();
     if (empId  == null || empId.isEmpty())  empId  = getLoginEmpId();

     businessMapper.initChurnStdrByVend(vendId, empId);

     int cnt = 0;

     if (dormancyList != null) {
         for (ChurnStdrVO vo : dormancyList) {
             vo.setVendId(vendId);
             vo.setUpdtBy(empId);
             cnt += businessMapper.updateChurnStdrByBizKey(vo);
         }
     }

     if (churnList != null) {
         for (ChurnStdrVO vo : churnList) {
             vo.setVendId(vendId);
             vo.setUpdtBy(empId);
             cnt += businessMapper.updateChurnStdrByBizKey(vo);
         }
     }

     return cnt;
 }





	//휴면,이탈검색조회
	@Override
	public List<churnRiskVO> getchurnRiskList(churnRiskVO vo) {
		 vo.setVendId(getLoginVendId());   // 세션 회사코드 세팅
		return businessMapper.getchurnRiskList(vo);
	}
	//휴면,이탈 평균구매주기
	@Override
	public int getAVG() {
		return businessMapper.getAVG();
	}
	// 매출변동 조회
	@Override
	public List<MonthlySalesDTO> getMonthlySalesChange(MonthlySalesDTO vo) {
	    String vendId = LoginSession.getVendId();
	    if (vendId == null || vendId.isEmpty()) {
	        throw new IllegalStateException("로그인 회사 코드(VEND_ID)가 없습니다.");
	    }
	    vo.setVendId(vendId);   // ★ 회사 기준 필터만 세팅
	    return businessMapper.getMonthlySalesChange(vo);
	}
 //
 @Override
 public List<BusinessVO> getList() {
     return businessMapper.getList();
 }
  @Override
	public int existsPotentialInfoNo(Long potentialInfoNo) {
		return 0;
	}
 
//공공데이터 잠개고객 데이터 매핑
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
 	
	  // ✅ 0) 조회조건에 로그인 회사 vendId 세팅(너 지금 조회쿼리가 회사필터가 없어서 필요하면 여기서 추가)
	    // cond.setVendId(getLoginVendId());  // 필요하면 BusinessVO에 vendId 조건 추가해서 mapper에서도 AND vend_id=... 걸어라
	    // 1) 조건에 맞는 잠재고객 리스트 조회
	    List<BusinessVO> list = businessMapper.getBusinessList(cond);
	    // 2) 로그인 회사 업종 가져오기
	    String loginIndustry = getLoginCompanyIndustry();
	    // ✅ 3) DB 기준(상세점수표) 한번에 조회
	    PotentialStdrVO stdrCond = new PotentialStdrVO();
	    stdrCond.setVendId(getLoginVendId());
	    List<PotentialStdrVO> stdrDetailList = businessMapper.getPotentialStdrDetailList(stdrCond);
	    // 4) 각 잠재고객 점수 계산
	    for (BusinessVO row : list) {
	        int score = aiService.calculateLeadScoreByIndustry(
	                loginIndustry,
	                row.getIndustryType(),
	                row.getCompanySize(),
	                row.getRegion(),
	                row.getEstablishDate(),
	                stdrDetailList
	        );
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
 // 잠재고객 기준상세목록 전제조회 (완)
 public List<PotentialStdrVO> getPotentialStdrDetailList(PotentialStdrVO cond) {
	  cond.setVendId(getLoginVendId());   // 회사코드 세팅
     return businessMapper.getPotentialStdrDetailList(cond);
 }
 // 잠재고객 기준상세목록 등록 및 수정(완) - I/U 통합 저장
 @Override
 @Transactional
 public void savePotentialStdrDetailList(List<PotentialStdrVO> list) {
     if (list == null || list.isEmpty()) return;

     String vendId = getLoginVendId();
     String empId  = getLoginEmpId();

     // ✅ 1) 최초 저장이면 기본 템플릿을 회사별로 전체 복사 (없으면 아무것도 안 함)
     businessMapper.initPotentialStdrByVend(vendId, empId);

     // ✅ 2) 변경분만 처리 (I/U/D)
     for (PotentialStdrVO row : list) {
         row.setVendId(vendId); // 무조건 로그인 회사 기준으로 저장

         String st = row.getRowStatus();
         if ("I".equals(st)) {
             row.setCreaBy(empId);
             businessMapper.insertPotentialStdrDetail(row);

         } else if ("U".equals(st)) {
             row.setUpdtBy(empId);
             businessMapper.updatePotentialStdrDetail(row);

         } else if ("D".equals(st)) {
             // D도 save로 처리하고 싶으면 여기서 삭제
             businessMapper.deletePotentialStdr(row);
         }
     }
 }

	//잠재고객 기준상세목록 삭제(완)
	@Override
	public int deletePotentialStdrList(List<String> idList) {
	   if (idList == null) return 0;
	
	   int cnt = 0;
	   String vendId = getLoginVendId();
	
	   for (String id : idList) {
	       PotentialStdrVO vo = new PotentialStdrVO();
	       vo.setStdrDetailId(id);
	       vo.setVendId(vendId);              // 회사코드 같이 넘김
	       businessMapper.deletePotentialStdr(vo);
	       cnt++;
	   }
	
	   return cnt;
	}
	@Override
	public List<BusinessVO> getcustaddrtype(String cond) {
	    List<BusinessVO> list = businessMapper.getcustaddrtype(cond);
	    String loginIndustry = LoginSession.getBizcnd();
	    // ✅ 기준 DB 조회 (회사 기준)
	    PotentialStdrVO stdrCond = new PotentialStdrVO();
	    stdrCond.setVendId(getLoginVendId());
	    List<PotentialStdrVO> stdrDetailList =
	            businessMapper.getPotentialStdrDetailList(stdrCond);
	    for (BusinessVO row : list) {
	        int score = aiService.calculateLeadScoreByIndustry(
	                loginIndustry,
	                row.getIndustryType(),
	                row.getCompanySize(),
	                row.getRegion(),
	                row.getEstablishDate(),
	                stdrDetailList     // ⭐ 이게 빠져 있었음
	        );
	        row.setLeadScore(score);
	    }
	    return list;
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
	public List<ContactVO> getContactListByVend(Integer potentialInfoNo) {
	    ContactVO cond = new ContactVO();
	    cond.setVendId(getLoginVendId());
	    cond.setPotentialInfoNo(potentialInfoNo);
	    return businessMapper.selectContactListByVend(cond);
	}
	//
	// 접촉내역 저장
	@Override
	@Transactional
	public void saveAll(String vendId, Integer potentialInfoNo, List<ContactVO> contactList) {
	    if (contactList == null) return;
	    String empId = getLoginEmpId(); // ★ 여기서 세션 사용
	    for (ContactVO row : contactList) {
	        row.setVendId(vendId);
	        row.setEmpId(empId);
	        row.setPotentialInfoNo(potentialInfoNo);
	        if (row.getContactNo() == null) {
	            businessMapper.insertContact(row);
	        } else {
	            businessMapper.updateContact(row);
	        }
	    }
	}
  //
	//리드내역 조회
	   @Override
	   public List<LeadVO> getLeadListByVend(Integer potentialInfoNo) {
	       LeadVO cond = new LeadVO();
	       cond.setVendId(getLoginVendId());
	       cond.setPotentialInfoNo(potentialInfoNo);
	       return businessMapper.getLeadListByVend(cond);
	   }
	//리드내역 저장
  @Override
  @Transactional
  public void saveAllLead(String vendId, Integer potentialInfoNo, List<LeadVO> list) {
      if (list == null) return;
      String loginVendId = getLoginVendId();
      String loginEmpId  = getLoginEmpId();
      for (LeadVO row : list) {
          row.setVendId(loginVendId);
          row.setPotentialInfoNo(potentialInfoNo);
          if (row.getLeadNo() == null) {
              row.setCreaBy(loginEmpId);
              businessMapper.insertLead(row);
          } else {
              row.setUpdtBy(loginEmpId);
              businessMapper.updateLead(row);
          }
      }
  }
	//
	//데모내역 조회
	@Override
	public List<DemoVO> getDemoListByVend(Integer potentialInfoNo) {
	    DemoVO cond = new DemoVO();
	    cond.setVendId(getLoginVendId());
	    cond.setPotentialInfoNo(potentialInfoNo);
	    return businessMapper.getDemoListByVend(cond);
	}
	//데모내역 저장
	@Override
	@Transactional
	public void saveAllDemo(String vendId, Integer potentialInfoNo, List<DemoVO> demolist) {
	    if (demolist == null) return;
	    String loginVendId = getLoginVendId();
	    String loginEmpId  = getLoginEmpId();
	    for (DemoVO row : demolist) {
	        row.setVendId(loginVendId);
	        row.setPotentialInfoNo(potentialInfoNo);
	        if (row.getDemoQuotatioNo() == null) {
	            row.setCreaBy(loginEmpId);
	            businessMapper.insertDemo(row);
	        } else {
	            row.setUpdtBy(loginEmpId);
	            businessMapper.updateDemo(row);
	        }
	    }
	}
}


