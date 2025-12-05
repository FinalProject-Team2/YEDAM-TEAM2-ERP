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
	int mlssVisitChk(String val);
}
