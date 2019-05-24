package com.jstarcraft.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ImportResource({ "classpath:applicationContext-configuration.xml", "classpath:applicationContext-cache.xml", "classpath:applicationContext-storage.xml" })
//// 解决Spring-Boot的JPA与核心框架的冲突
//@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
public class ExampleApplication {

    public static void main(String[] arguments) {
        SpringApplication.run(ExampleApplication.class, arguments);
    }

}
