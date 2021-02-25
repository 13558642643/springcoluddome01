package org.example.dao;

import org.example.entity.Goods;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-02-25 14:42
 * @Description :
 */
@Repository
public interface GoodsDao {
    Goods getGoodsById(@PathParam("id") Integer id);
}
