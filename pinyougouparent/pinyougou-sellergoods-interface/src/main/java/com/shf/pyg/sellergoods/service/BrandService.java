package com.shf.pyg.sellergoods.service;

import com.shf.pyg.pojo.TbBrand;

import java.util.List;

/**
 * 品牌业务接口
 */
public interface BrandService {
    public List<TbBrand> findAll();
}
