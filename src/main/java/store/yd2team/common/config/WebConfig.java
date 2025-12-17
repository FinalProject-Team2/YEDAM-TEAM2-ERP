package store.yd2team.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import store.yd2team.common.interceptor.LoginCheckInterceptor;
import store.yd2team.common.interceptor.MenuAuthInterceptor;
import store.yd2team.common.interceptor.PageTitleInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
	
	private final LoginCheckInterceptor loginCheckInterceptor;
	private final MenuAuthInterceptor menuAuthInterceptor;
	 private final PageTitleInterceptor pageTitleInterceptor;
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		System.out.println("========================"+System.getProperty("user.dir"));
		String baseDir = System.getProperty("user.dir") + "\\upload\\";
		 //String uploadDir = System.getProperty("user.dir") + "\\upload\\images\\";
			/*
			 * registry.addResourceHandler("/images/**") // 해당 경로의 요청이 올 때
			 * .addResourceLocations("file:/"+uploadDir) // classpath 기준으로 'm' 디렉토리 밑에서 제공
			 * "file:/C:\\Users\\admin\\git\\YEDAM-TEAM2-ERP\\upload\\images\\"
			 * .setCachePeriod(20); // 캐싱 지정
			 */    
		// 이미지 매핑 유지
	        registry.addResourceHandler("/images/**")
	                .addResourceLocations("file:/" + baseDir + "images/")
	                .setCachePeriod(20);

	        // PDF 매핑 추가
	        registry.addResourceHandler("/pdf/**")
	                .addResourceLocations("file:/" + baseDir + "pdf/")
	                .setCachePeriod(20);

	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

	    registry.addInterceptor(loginCheckInterceptor)
	            .order(1)
	            .addPathPatterns("/**")
	            .excludePathPatterns(
	                    "/logIn/**",
	                    "/signUp/**",
	                    "/assets/**",
	                    "/css/**",
	                    "/js/**",
	                    "/images/**",
	                    "/pdf/**",        // ✅ pdf 리소스면 보통 제외하는 게 안전
	                    "/error",
	                    "/favicon.ico"
	            );

	    registry.addInterceptor(menuAuthInterceptor)
	            .order(2)
	            .addPathPatterns("/**")
	            .excludePathPatterns(
	                    "/logIn/**",
	                    "/signUp/**",
	                    "/assets/**",
	                    "/css/**",
	                    "/js/**",
	                    "/images/**",
	                    "/pdf/**",
	                    "/error",
	                    "/favicon.ico"
	            );
	    
	    registry.addInterceptor(pageTitleInterceptor)
        	.addPathPatterns("/**");
	}
}
