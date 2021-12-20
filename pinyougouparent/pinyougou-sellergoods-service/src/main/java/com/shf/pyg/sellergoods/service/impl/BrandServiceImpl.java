package com.shf.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shf.pyg.mapper.TbBrandMapper;
import com.shf.pyg.pojo.TbBrand;
import com.shf.pyg.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 品牌服务层实现
 */
@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }
}
