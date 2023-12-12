package com.example.train.business.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.example")
// 不同模块连接不同数据库--所以配置文件应该不同才行，不能放在common
@MapperScan("com.example.train.business.mapper")
@EnableFeignClients("com.example.train.business.feign")
public class BussinessApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BussinessApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BussinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("测试地址: \thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));
    }
}
