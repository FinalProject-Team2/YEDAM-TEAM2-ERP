package store.yd2team.common;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import store.yd2team.common.mapper.SampleMapper;


@SpringBootTest
public class SampleMapperTest {

	@Autowired SampleMapper sampleMapper;	
	
	@Test
	@Disabled
	public void test() {
		String str = sampleMapper.sample();
		System.out.println(str);
	}
	
	
	
}
