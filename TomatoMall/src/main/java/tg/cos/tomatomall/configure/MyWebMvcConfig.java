package tg.cos.tomatomall.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns("/api/accounts/login")//对createUser的处理在LoginInterceptor的prehandle中
                .excludePathPatterns("/")
                .excludePathPatterns("/api/orders/notify")
                .excludePathPatterns("/api/orders/returnUrl")
                .order(1);
    }
}
