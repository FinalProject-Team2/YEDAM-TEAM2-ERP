package store.yd2team.insa.service;

import java.util.List;

public interface VcatnService {

	List<VcatnVO> vcatnListVo(VcatnVO val);
	
	//사용자가 페이지 방문시마다 사용자의 남은 연차 갯수 불러오는 서비스
	int yrycUserRemndrChk(String val);
	
	//휴가등록
	String vcatnRegist(VcatnVO val);
	
	//휴가등록취소
	int vcatnRollback(VcatnVO val);
	
	//관리자권한의 휴가처리 승인 및 반려기능
	int vcatnCfmUpdate(VcatnVO val);
	
	
}
