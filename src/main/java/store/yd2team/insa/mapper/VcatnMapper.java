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

}
