package com.ly.lyaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.ly.lyaicodemother.mapper")
public class LyAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyAiCodeMotherApplication.class, args);
    }

}
