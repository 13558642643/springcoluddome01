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
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.nio.charset.Charset;
import java.util.ArrayList;
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

    private static  String STOCK_LUA = "" ;

    private static  String STOCK_LUA_1 = "" ;

    private static  String STOCK_LUA_INCR = "" ;

    static {
        /**
         *
         * @desc 扣减库存Lua脚本
         * 库存（stock）-1：表示不限库存
         * 库存（stock）0：表示没有库存
         * 库存（stock）大于0：表示剩余库存
         *
         * @params 库存key
         * @return
         * 		-3:库存未初始化
         * 		-2:库存不足
         * 		-1:不限库存
         * 		大于等于0:剩余库存（扣减之后剩余的库存）
         * 	    redis缓存的库存(value)是-1表示不限库存，直接返回1
         */
        StringBuilder sb = new StringBuilder();
        sb.append("if (redis.call('exists', KEYS[1]) == 1) then");
        sb.append("    local stock = tonumber(redis.call('get', KEYS[1]));");
        sb.append("    local num = tonumber(ARGV[1]);");
        sb.append("    if (stock == -1) then");
        sb.append("        return -1;");
        sb.append("    end;");
        sb.append("    if (stock >= num) then");
        sb.append("        return redis.call('incrby', KEYS[1], 0 - num);");
        sb.append("    end;");
        sb.append("    return -2;");
        sb.append("end;");
        sb.append("return -3;");
        STOCK_LUA = sb.toString();

        StringBuilder sb1 = new StringBuilder();
        sb1.append("if (redis.call('exists', KEYS[1]) == 1) then");
        sb1.append("    local stock = tonumber(redis.call('get', KEYS[1]));");
        sb1.append("    local num = tonumber(ARGV[1]);");
        sb1.append("    if (stock >= num) then");
        sb1.append("        return redis.call('incrby', KEYS[1], 0 - num);");
        sb1.append("    end;");
        sb1.append("    return -2;");
        sb1.append("end;");
        sb1.append("return -3;");
        STOCK_LUA_1 = sb1.toString();

        StringBuilder sb2 = new StringBuilder();
        sb2.append("if (redis.call('exists', KEYS[1]) == 1) then");
        sb2.append("    local stock = tonumber(redis.call('get', KEYS[1]));");
        sb2.append("    local num = tonumber(ARGV[1]);");
        sb2.append("    if (stock >= 0) then");
        sb2.append("        return redis.call('incrby', KEYS[1], num);");
        sb2.append("    end;");
        sb2.append("    return -2;");
        sb2.append("end;");
        sb2.append("return -3;");
        STOCK_LUA_INCR = sb2.toString();
    }

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
        boolean isUser =  redisTemplate.hasKey(KillConstants.KILLED_GOOD_USER + goodsId+UserId);

        if(isUser){
            log.info("【用户】+"+UserId+"【重复秒杀】");
            return false;
        }
        String killGoodCount = KillConstants.KILL_GOOD_COUNT + goodsId;
        Long result =  stock(killGoodCount,1,STOCK_LUA);
        System.out.println("【返回值】："+result);
        if(0 <= result){
            System.out.println("记录用户");
            redisTemplate.opsForValue().set(KillConstants.KILLED_GOOD_USER + goodsId+UserId,UserId);
            return true;
        }

        return false;
    }


    /**
     *
     * @param goodId 扣库存的ID
     * @param num 扣库存的数量
     * @param script
     *
     * @return 扣减之后剩余的库存【-3:库存未初始化; -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存】
     */
    public  Long stock(String goodId, int num, String script){
        // 脚本里的KEYS参数
        List<String> keys = new ArrayList<>();
        keys.add(goodId);
        // 脚本里的ARGV参数
        List<String> args = new ArrayList<>();
        args.add(Integer.toString(num));

        Long result =  (Long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Object nativeConnection = redisConnection.getNativeConnection();
                if(nativeConnection instanceof Jedis){
                    System.out.println("【单机JEDIS】");
                    return (Long)((Jedis) nativeConnection).eval(script,keys,args);
                }
                return 0;
            }
        });

        return result;
    }
}
