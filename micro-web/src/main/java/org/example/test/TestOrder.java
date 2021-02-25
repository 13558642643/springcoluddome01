package org.example.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-02-22 14:38
 * @Description :
 */

public class TestOrder {

    public static String SERVIER_NAME = "micro-order";

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test01() throws  InterruptedException{


        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                String results = restTemplate.getForObject("http://"
                        + "127.0.0.1:8084" + "/queryUser", String.class);
                System.out.println(results);
            }).start();
            Thread.sleep(1000);
        }

    }


}
