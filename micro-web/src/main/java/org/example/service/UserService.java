package org.example.service;


import org.example.vo.Goods;

public interface UserService {
    String queryContents();


    Goods detail(int killId);


    boolean killGood(Integer goodsId ,Integer UserId);

}
