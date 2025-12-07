package store.yd2team.insa.service;

import java.util.List;
import java.util.Map;

public interface MlssService {

	//다면평가 신규등록
	int mlssRegist(MlssVO val);
	
	//다면평가 등록된 리스트 다중검색조건으로 불러오기
	List<MlssVO> mlssListJohoe(MlssVO val);
	
	//다면평가 페이지 로드될 때 불러올 기본정보 처리
	Map<String, List<MlssVO>> mlssLoadBefore();
	
	//다면평가 페이지 방문시 평가 기간인지 아닌지 확인하기
	String mlssVisitChk(String val);
	
	//다면평가 상사 및 동료 리스트 불러오기
	List<EmpVO> mlssEmpList(String val);
	
	//다면평가 기평가 리스트 불러오기
	List<MlssVO> mlssWrterLoadBefore(String mlssId, String empId);
	
	//작성이력 테이블관련
	//다면평가 작성할 때 쓰이는 메소드
	int mlssWrterRegist(MlssRequestVO val);
}
