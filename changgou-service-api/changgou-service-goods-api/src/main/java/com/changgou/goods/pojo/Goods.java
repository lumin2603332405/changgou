package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.goods.pojo
 * @version 1.0
 * @date 2019/12/30
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
