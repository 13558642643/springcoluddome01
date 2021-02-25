package org.example.controller;

import org.example.entity.Goods;
import org.example.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-01-04 10:42
 * @Description :
 */
//@Slf4j
@RestController
public class UserController {

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/queryUser")
    public String queryUser() {
        //log.info("========micro-order===queryUser");
        System.out.println("开始");
        Goods goods = goodsService.getGoodsById(1);
        System.out.println(goods);
        return "开始";
    }

    @RequestMapping("/queryGoodsById")
    public Object queryGoodsById(@PathParam("id") Integer id) {
        System.out.println("看看："+id);
        Goods goods = goodsService.getGoodsById(id);
        System.out.println(goods);
        return goods;
    }
}