package org.example.entity;

import java.io.Serializable;

/**
 * @author : ZXW
 * @version : 1.0
 * @date : 2021-02-24 15:12
 * @Description :
 */
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;
private Integer id;
    private String goodsName;
    private String originalImg;
    private String key;
    private String keyName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOriginalImg() {
        return originalImg;
    }

    public void setOriginalImg(String originalImg) {
        this.originalImg = originalImg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }


    @Override
    public String toString() {
        return "Goods{" +
                "goodsName='" + goodsName + '\'' +
                ", originalImg='" + originalImg + '\'' +
                ", key='" + key + '\'' +
                ", keyName='" + keyName + '\'' +
                '}';
    }
}
