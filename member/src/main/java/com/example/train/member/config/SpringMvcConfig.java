package com.example.train.member.config;

import com.example.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//  开启哪个拦截器由每个包自己决定。因此注释写在这里
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

//   @Resource
//   LogInterceptor logInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
//       开启 memberInterceptor

       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/member/hello",
                       "/member/member/send-code",
                       "/member/member/login"
               );
   }
}
