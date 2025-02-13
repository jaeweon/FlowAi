package com.board.flowai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class FlowAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowAiApplication.class, args);
    }

}
