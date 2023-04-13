package net.syl.dandelion.cron;

import net.syl.dandelion.cron.xxl.client.JobInfoClient;
import net.syl.dandelion.service.client.SendClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(clients = {JobInfoClient.class, SendClient.class})
@ComponentScan(basePackages = {"net.syl.dandelion.support","net.syl.dandelion.cron"})
@MapperScan(basePackages = "net.syl.dandelion.support.mapper")
public class CronApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronApplication.class, args);
    }
}
