package com.example.train.batch.feign;

import com.example.train.common.resp.CommonResp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
// 客户端 调用business
//@FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
@FeignClient(value = "business")
public interface BusinessFeign {
//  nacos 的name business 仅能确定到ip：port，并不能到/business
    @GetMapping("/business/hello")
    String hello();

    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
