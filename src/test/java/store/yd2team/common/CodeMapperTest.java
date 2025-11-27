package store.yd2team.common;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import store.yd2team.common.mapper.CodeMapper;
import store.yd2team.common.service.CodeVO;

@SpringBootTest
public class CodeMapperTest {
	
	@Autowired CodeMapper codeMapper;
	
	@Test
	public void test() {
		
		CodeVO condition = new CodeVO();
		condition.setGrpNm("가");
		
		List<CodeVO> list = codeMapper.findGrp(condition);
		
		list.forEach(e -> System.out.println(e));
		
		
		
	}
	
}
