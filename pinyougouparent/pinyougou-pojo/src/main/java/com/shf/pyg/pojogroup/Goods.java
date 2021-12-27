package com.shf.pyg.pojogroup;

import com.shf.pyg.pojo.TbGoods;
import com.shf.pyg.pojo.TbGoodsDesc;

import java.io.Serializable;

/**
 * 商品组合实体类
 */
public class Goods implements Serializable {
    private TbGoods goods;
    private TbGoodsDesc goodsDesc;

    public Goods() {
    }

    public Goods(TbGoods goods, TbGoodsDesc goodsDesc) {
        this.goods = goods;
        this.goodsDesc = goodsDesc;
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goods=" + goods +
                ", goodsDesc=" + goodsDesc +
                '}';
    }
}
