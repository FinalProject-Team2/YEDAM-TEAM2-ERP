package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import store.yd2team.insa.service.EdcVO;
import store.yd2team.insa.service.EmpVO;
@Mapper
public interface EdcMapper {

	//다중조건 조회
		List<EdcVO> getListEdcJohoe(EdcVO keyword);
		
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
		
	
	//User전용 쿼리모음
	//유저전용 다중검색 조회
		List<EdcVO> getListEdcUserJohoe(EdcVO keyword);
		
	//유저법정교육대상자 pdf등록할때 쓰는 update쿼리
		int edcUserRegistChk(EdcVO keyword);
	
}
