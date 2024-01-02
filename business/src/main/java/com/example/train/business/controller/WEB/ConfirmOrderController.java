package com.example.train.business.controller.WEB;

import com.example.train.business.req.ConfirmOrderDoReq;
//import com.example.train.business.service.ConfirmOrderService;
import com.example.train.business.service.ConfirmOrderService1;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

//    @Resource
//    private ConfirmOrderService1 confirmOrderService;

    @Resource
    private ConfirmOrderService1 confirmOrderService1;

    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) throws Exception {
//        System.out.println(req);
        Long id = LoginMemberContext.getId();
        req.setMemberId(id);
        confirmOrderService1.doConfirm(req);
        return new CommonResp<>();
    }

//    @GetMapping("/cancel/{id}")
//    public CommonResp<Integer> cancel(@PathVariable Long id) {
//        Integer count = confirmOrderService.cancel(id);
//        return new CommonResp<>(count);
//    }
}
