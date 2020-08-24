package per.cjh.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import per.cjh.example.interceptor.MyInterceptor;

/**
 * @author cjh
 * @description: 配置自定义的拦截器（拦截未登录的请求）和 前后端联调的跨域请求支持
 * @date 2020/5/4 13:47
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                /*
                 下面的.allowCredentials(true)：表示允许跨域携带 cookie，前端不同框架得实现自带 cookie的功能，比如 jQuery框架如下：
                    xhrFields: {
                        withCredentials: true,
                    },
                */
                .allowCredentials(true);
    }

    //         打开即可开启过滤器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor()).
                addPathPatterns("/*/*.html").
                excludePathPatterns("/",
                        "/index.html",
                        "/*/main.html",
                        "/*/newsInfo.html",
                        "/*/teacherForgetPassword.html",
                        "/*/stuForgetPassword.html",
                        "/*/video.html",
                        "/*/news.html",
                        "/*/notice.html",
                        "/*/schedule.html",
                        "/*/findPassword.html");
    }
}
