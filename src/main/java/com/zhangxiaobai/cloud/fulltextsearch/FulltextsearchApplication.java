package com.zhangxiaobai.cloud.fulltextsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FulltextsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FulltextsearchApplication.class, args);
    }

}
