package com.jstarcraft.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource
// 解决Spring-Boot的JPA与核心框架的冲突
@EnableAutoConfiguration
public class ExampleApplication {

    public static void main(String[] arguments) {
        SpringApplication.run(ExampleApplication.class, arguments);
    }

}
