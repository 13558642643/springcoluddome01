package org.example.service;

//import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.example.constant.KillConstants;
import org.example.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
public class UserServiceImpl implements UserService {

    private AtomicInteger s = new AtomicInteger();
    private AtomicInteger f = new AtomicInteger();

    public static String SERVIER_NAME = "micro-order";

    private static Object obj = new Object();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String queryContents() {
        s.incrementAndGet();
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
            if(httpMessageConverter instanceof StringHttpMessageConverter){
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
        String results = restTemplate.getForObject("http://"
                + SERVIER_NAME + "/queryGoodsById?id=2", String.class);
        log.info(results);
        return results;
    }

    private Goods getGoodsById(Integer id){
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
            if(httpMessageConverter instanceof StringHttpMessageConverter){
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
        String results = restTemplate.getForObject("http://"
                + SERVIER_NAME + "/queryGoodsById?id="+id, String.class);
        System.out.println("【results】"+results);
        return JSONObject.parseObject(results,Goods.class);
    }

    @Override
    public Goods detail(int killId) {
        redisTemplate.opsForValue().set("nlx", "nlxStr", 2, TimeUnit.MINUTES);
        Goods goods = null;
        String strForm = "";
        //拼接在Redis中的字符串
        String goodsDetail = KillConstants.KILLGOOD_DETAIL + killId;

        //1.查询缓存中是否存在数据
        Cache goodCache =  cacheManager.getCache("killgoodDetail");
        if(null != goodCache.get(goodsDetail)){
            log.info(Thread.currentThread().getName() +"--------------------ehcache获取数据  ");
            goods = (Goods)goodCache.get(goodsDetail).getObjectValue();
            return goods;
        }

        //2.在Redis中查找数据是否存在
        Object objRedisTemplate =  redisTemplate.opsForValue().get(goodsDetail);
        if(null != objRedisTemplate){
            log.info(objRedisTemplate.toString());
            log.info(Thread.currentThread().getName() +"--------------------Redis获取数据");

            return  JSONObject.parseObject(objRedisTemplate.toString(),Goods.class);
        }

        synchronized (obj){
            //1.去数据库查询数据，并保存到缓存中
            Goods newGoods = getGoodsById(killId);
//            newGoods.setGoodsName("暖宝宝");
//            newGoods.setKey("1");
//            newGoods.setOriginalImg("F:/图片.png");
//            newGoods.setKeyName("123");
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("【1.去数据库查询数据，并保存到缓存中】"+newGoods.toString());
            goodCache.putIfAbsent(new Element(goodsDetail,newGoods));
            Object jsonStr = JSONArray.toJSON(newGoods);
            redisTemplate.opsForValue().set(goodsDetail, jsonStr, 2, TimeUnit.MINUTES);
            goods = newGoods;
        }



        return goods;
    }


    @Override
    public boolean killGood(Integer goodsId ,Integer UserId){
        //不准重复秒杀
        boolean isUser =  redisTemplate.opsForSet().isMember(KillConstants.KILLED_GOOD_USER + goodsId, UserId);

        if(isUser){
            log.info("【用户】+"+UserId+"【重复秒杀】");
            return false;
        }
        String killGoodCount = KillConstants.KILL_GOOD_COUNT + goodsId;
        return false;
    }
}
