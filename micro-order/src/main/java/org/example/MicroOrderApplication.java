package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-01-04 10:41
 * @Description :
 */
@SpringBootApplication
//开启eureka客户端功能
@EnableEurekaClient
@MapperScan("org.example.dao")
public class MicroOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroOrderApplication.class,args);
    }
}
