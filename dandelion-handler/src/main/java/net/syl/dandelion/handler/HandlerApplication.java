package net.syl.dandelion.handler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableRabbit
@EnableDiscoveryClient
@ComponentScan(basePackages = {"net.syl.dandelion.support","net.syl.dandelion.handler"})
@MapperScan(basePackages = "net.syl.dandelion.support.mapper")
public class HandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandlerApplication.class, args);
    }
}
