package com.example.train.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
// 这个域可以被实时的刷新
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!"+testNacos;
    }

    @Value("${test.nacos}")
    private String testNacos;

}
