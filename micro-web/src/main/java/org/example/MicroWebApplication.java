package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication

@EnableEurekaClient
public class MicroWebApplication {

    @Bean
    //负载均衡注解
    @LoadBalanced
    RestTemplate restTemplate() {
       return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MicroWebApplication.class,args);
    }
}
