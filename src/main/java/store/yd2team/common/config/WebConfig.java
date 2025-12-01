package store.yd2team.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		System.out.println(System.getProperty("user.dir"));
		 String uploadDir = System.getProperty("user.dir") + "\\upload\\images\\";
        registry.addResourceHandler("/images/**")           // 해당 경로의 요청이 올 때
                .addResourceLocations("file:/"+uploadDir) // classpath 기준으로 'm' 디렉토리 밑에서 제공 "file:/C:\\Users\\admin\\git\\YEDAM-TEAM2-ERP\\upload\\images\\"
                .setCachePeriod(20);                   // 캐싱 지정
    }
}
