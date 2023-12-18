package com.example.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.common.ExceptionHandler.ControllerExceptionHandler;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @GetMapping("/hello")
    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public String hello(int a) {
//      被调用的方法的返回值和参数都必须与原方法相同。即都需要返回String
        String s = hello1("c");
        return s;
    }

//    @SentinelResource(value = "doConfirm", blockHandler = "doConfirmBlock")
    public String hello1(String a){
        return "Fuck!!!"+a;
    }

    public String doConfirmBlock(int a,BlockException e) {
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
