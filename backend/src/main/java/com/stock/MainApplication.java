package com.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;


@SpringBootApplication
@MapperScan("com.stock.mapper")
public class MainApplication {

    public static void main(String[] args) {
        String version= SpringVersion.getVersion();
        System.out.println(version);
        SpringApplication.run(MainApplication.class, args);
    }
}
