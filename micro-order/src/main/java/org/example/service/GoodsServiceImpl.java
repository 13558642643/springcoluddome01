package org.example.service;

import org.example.dao.GoodsDao;
import org.example.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-02-25 14:56
 * @Description :
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public Goods getGoodsById(Integer id) {
        System.out.println("==================================【id】"+id);
        return goodsDao.getGoodsById(id);
    }
}
