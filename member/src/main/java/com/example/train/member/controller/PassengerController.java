package com.example.train.member.controller;

import com.example.train.common.resp.CommonResp;

import com.example.train.member.req.PassengerSaveReq;

import com.example.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    public PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody  PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp();
    }
}
