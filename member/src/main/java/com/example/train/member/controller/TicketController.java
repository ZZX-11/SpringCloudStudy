package com.example.train.member.controller;

import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.resp.CommonResp;
import com.example.train.common.resp.PageResp;
import com.example.train.member.req.TicketQueryReq;
import com.example.train.member.resp.TicketQueryResp;
import com.example.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// my-ticket 页面

@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;
//  某个人的会员车票
    @GetMapping("/ticket/query-list")
    public CommonResp<PageResp<TicketQueryResp>> query(@Valid TicketQueryReq req) {
        CommonResp<PageResp<TicketQueryResp>> commonResp = new CommonResp<>();
        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketQueryResp> pageResp = ticketService.queryList(req);
        commonResp.setContent(pageResp);
        return commonResp;
    }

//    全部的会员车票
    @GetMapping("/admin/ticket/query-list-A")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        PageResp<TicketQueryResp> list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/admin/ticket/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return new CommonResp<>();
    }
}
