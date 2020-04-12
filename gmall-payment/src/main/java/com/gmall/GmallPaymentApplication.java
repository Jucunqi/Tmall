package com.gmall;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import tk.mybatis.spring.annotation.MapperScan;

@EnableJms
@EnableDubbo
@MapperScan("com.gmall.payment.mapper")
@SpringBootApplication
public class GmallPaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallPaymentApplication.class);
    }
}
