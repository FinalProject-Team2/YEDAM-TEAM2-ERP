package store.yd2team.insa.service;

import java.util.List;
import java.util.Map;

public interface EdcService {

	//다중조건조회
		List<EdcVO> getListEdcJohoe(EdcVO edc);
		
	//교육별 선정된 대상자 목록조회
		List<EdcVO> getListEdcDetaJohoe(EdcVO edc);
		
	//모달에서 input에 옵션목록불러오기
		Map<String, Object> getInputOption();
		
	//모달에서 등록 눌렀을 때 db에 인서트하기
		int setDbEdcAdd(EdcVO edc);
		
	//법정교육 사용자용 쿼리 밑에 모음
	//다중조건조회(사용자용)
		List<EdcVO> getListEdcUserJohoe(EdcVO edc);
		
	//유저법정교육대상자 pdf등록할때 쓰는 update쿼리
		int edcUserRegistChk(EdcVO edc);
}
