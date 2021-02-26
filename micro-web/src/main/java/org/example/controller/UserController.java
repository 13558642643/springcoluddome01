package org.example.controller;

import org.example.service.UserService;
import org.example.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/queryUser")
    public String queryUser() {
        return userService.queryContents();
    }


    @RequestMapping("/kill")
    public Object killGoods(){
//        System.out.println("【康康】]");
        Integer killId = 1;
//        System.out.println("看看："+killId);

        Goods killGoods = userService.detail(killId);
        if(null == killGoods){
            return "商品不存在";
        }
        boolean checkKillGood = userService.killGood(killId,10086);
        return killGoods;
    }
}
