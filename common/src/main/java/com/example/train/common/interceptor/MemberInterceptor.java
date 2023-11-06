package com.example.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.resp.MemberLoginResp;
import com.example.train.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//      当前登录用户的ID在很多个接口和文件都可能用到，所以写在common中拦截
//      拦截器的开启需要每个用户自己指定，所以config写在每个模块中。
//      同时
@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取header的token参数
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LOG.info("获取会员登录token：{}", token);
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            LOG.info("当前登录会员：{}", loginMember);
//            JSONUtil是Hutool提供的一个用于处理JSON数据的工具类，它可以将JSON数据转换为Java对象。
//            具体而言，toBean方法用于将JSON字符串转换为指定的Java对象。
//            在这里，代码将loginMember这个JSON字符串转换为了MemberLoginResp类的实例对象。
//            通过这种方式，可以方便地将JSON数据转换为Java对象，进而进行后续的操作和处理。
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            LoginMemberContext.setMember(member);
        }
        return true;
    }

}
