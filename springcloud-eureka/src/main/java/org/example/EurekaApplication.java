package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-01-04 10:29
 * @Description :
 */
@SpringBootApplication
//开启eurekaServer服务注册功能
@EnableEurekaServer
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class,args);
    }
}
