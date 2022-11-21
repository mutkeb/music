//package fm.douban.app.config;
//
//
//import fm.douban.app.interceptor.UserInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class appConfigurer implements WebMvcConfigurer {
//    @Override
//    public void addInterceptors(InterceptorRegistry registry){
//        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/**")
//                .excludePathPatterns("/authenticate") // 登录操作不需要登录
//                .excludePathPatterns("/login")        // 登录页面不需要登录
//                .excludePathPatterns("/sign")        // 登录页面不需要登录
//                .excludePathPatterns("/register")        // 登录页面不需要登录
//                .excludePathPatterns("/css/**")          // 静态资源为文件不需要登录
//    }
//}
