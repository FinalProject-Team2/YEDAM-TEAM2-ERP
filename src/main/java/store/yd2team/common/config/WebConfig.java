package store.yd2team.common.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer{

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 String uploadDir = System.getProperty("user.dir") + "/upload/images/profil/";
        registry.addResourceHandler("/images/**")           // 해당 경로의 요청이 올 때
                .addResourceLocations("file://"+uploadDir) // classpath 기준으로 'm' 디렉토리 밑에서 제공
                .setCachePeriod(20);                   // 캐싱 지정
    }
}
