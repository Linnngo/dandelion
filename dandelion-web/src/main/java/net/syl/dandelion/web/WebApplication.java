package net.syl.dandelion.web;

import net.syl.dandelion.service.client.SendClient;
import net.syl.dandelion.web.client.CronClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {CronClient.class, SendClient.class})
@ComponentScan(basePackages = {"net.syl.dandelion.web", "net.syl.dandelion.support"})
@MapperScan(basePackages = "net.syl.dandelion.support.mapper")
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
