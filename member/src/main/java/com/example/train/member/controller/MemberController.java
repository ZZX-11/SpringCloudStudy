package com.example.train.member.controller;

import com.example.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/member")
// member 模块下的member表。第二个代表是哪个表
public class MemberController {
    @Resource
    public MemberService memberService;

    @GetMapping("/count")
    public Integer count() {
        return memberService.count();
    }
}
