package store.yd2team.insa.mapper;

import java.util.List;

import store.yd2team.insa.service.EdcVO;
import store.yd2team.insa.service.EmpVO;

public interface EdcMapper {

	//다중조건 조회
		List<EdcVO> getListEdcJohoe();
		
	//교육별 선정된 대상자 목록조회
		List<EdcVO> getListEdcDetaJohoe(EdcVO keyword);
		
	//edcId생성메소드
		String setDbEdcAddId();
		
	//교육프로그램 등록
		int setDbEdcAdd(EdcVO keyword);
		
	//교육선정대상 목록 받아오기
		List<EdcVO> getEdcPickList(EmpVO keyword);
		
	//edcTTId생성메소드
		String setDbEdcTTAddId();
		
	//edcTT다중인서트문
		int insertEdcTrgterList(List<EdcVO> keyword);
	
}
