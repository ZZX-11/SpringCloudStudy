package com.example.train.business.controller.WEB;

import com.example.train.business.req.ConfirmOrderDoReq;
//import com.example.train.business.service.ConfirmOrderService;
import com.example.train.business.service.ConfirmOrderService1;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

//    @Resource
//    private ConfirmOrderService1 confirmOrderService;

    @Resource
    private ConfirmOrderService1 confirmOrderService1;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderController.class);
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) throws Exception {

        // 图形验证码校验
        String imageCodeToken = req.getImageCodeToken();
        String imageCode = req.getImageCode();
        String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
        LOG.info("从redis中获取到的验证码：{}", imageCodeRedis);
        if (ObjectUtils.isEmpty(imageCodeRedis)) {
            return new CommonResp<>(false, "验证码已过期", null);
        }
        // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
        if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
            return new CommonResp<>(false, "验证码不正确", null);
        } else {
            // 验证通过后，移除验证码
            redisTemplate.delete(imageCodeToken);
        }

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
