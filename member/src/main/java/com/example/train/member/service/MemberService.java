package com.example.train.member.service;

import com.example.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;

    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);
//  或者@sl4j

    public int count(){
        int count = memberMapper.count();
        return count;
    }

}
