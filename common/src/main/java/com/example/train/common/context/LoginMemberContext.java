package com.example.train.common.context;

import com.example.train.common.resp.MemberLoginResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginMemberContext {
//  当前登录用户的ID在很多个接口和文件都可能用到，所以写在common中拦截
    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberContext.class);

//    ThreadLocal是Java中的一个线程局部变量，它提供了一种在多线程环境下存储和访问线程本地数据的机制。每个线程都有自己独立的ThreadLocal实例，可以通过该实例存储和获取线程本地的数据。

//在给定的代码中，ThreadLocal<MemberLoginResp>创建了一个ThreadLocal实例，其泛型类型指定为MemberLoginResp，
// 意味着每个线程都可以在该ThreadLocal对象中存储和获取一个MemberLoginResp类型的变量。
    private static ThreadLocal<MemberLoginResp> member = new ThreadLocal<>();

    public static MemberLoginResp getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginResp member) {
        LoginMemberContext.member.set(member);
    }

    public static Long getId() {
        try {
            return member.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}
