package store.yd2team.insa.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import store.yd2team.insa.service.VcatnVO;

@Mapper
public interface VcatnMapper {

	//다중조건 조회
			List<VcatnVO> getListVcatnJohoe(VcatnVO keyword);
			
	//사용자의 사용가능한 남은 연자갯수 조회
			int yrycUserRemndrChk(@Param("empId") String empId);
	
	//휴가등록(연차소진)
			int vcatnCreateData(VcatnVO val);
			
	//휴가등록yryc에 업데이트용 메소드
			int vcatnUpdateYrycData(@Param("empId") String empId,
                    @Param("vcatnDe") int vcatnDe);
			
	//vcatn등록했던 건 삭제
			int vcateDel(VcatnVO val);
			
	//yryc연차소모된만큼 다시 롤백쿼리
			int yrycRollback(VcatnVO val);
			
	//관리자가 휴가처리 승인/반려처리
			int vcatnCfmUpdate(VcatnVO val);

}
